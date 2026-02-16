# E-Commerce Spring Boot API - Documentation

## Project Overview
A RESTful API for an e-commerce platform built with Spring Boot, featuring user authentication, product management, and order processing with role-based access control.

---

## Technology Stack
- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL
- **Security:** Spring Security with HTTP Basic Authentication
- **ORM:** Hibernate/JPA
- **Password Encoding:** BCrypt (strength: 12)
- **Session Management:** Stateless (REST API)

---

## Design Choices

### 1. Authentication & Authorization

#### Choice: HTTP Basic Authentication
**Rationale:**
- Simple to implement and test with Postman
- Suitable for API-to-API communication
- Stateless - no session management needed
- Easy to understand for learning purposes

**Alternative Considered:** JWT tokens were considered but deemed unnecessarily complex for this project scope.

#### Choice: Role-Based Access Control (RBAC)
**Implementation:**
- Two roles: ADMIN and USER
- Roles stored in database with many-to-many relationship with users
- Uses Spring Security's `@PreAuthorize` annotations for method-level security

**Design Pattern:**
```
@PreAuthorize("hasRole('ADMIN')")  // Declarative security at controller level
+ Service-level ownership checks     // Business logic validation
```

### 2. Security Design Decisions

#### Password Storage
**Choice:** BCrypt with strength 12
**Rationale:**
- Industry standard for password hashing
- Built-in salt generation
- Computationally expensive enough to resist brute-force attacks
- Strength 12 balances security and performance

#### Circular Dependency Resolution
**Choice:** `@Lazy` annotation on UserDetailsService injection
**Rationale:**
- Breaks circular dependency: SecurityConfig → UserService → BCryptPasswordEncoder → SecurityConfig
- Cleaner than separating config classes
- Minimal performance impact

#### Session Management
**Choice:** Stateless sessions (`SessionCreationPolicy.STATELESS`)
**Rationale:**
- RESTful API design principle
- Better scalability (no server-side session storage)
- Each request contains authentication credentials
- Suitable for microservices architecture

### 3. Order Processing Security

#### Critical Security Design: Server-Side Price Calculation

**Solution Implemented:**
- Users only send: `productId` and `quantity`
- Server fetches actual prices from Product database
- Server calculates total amount
- Server sets order date (current timestamp)
- Server sets initial status (always PENDING)

**Request DTO Design:**
```java
// What user sends (minimal and safe)
{
    "orderItems": [
        {
            "productId": 2,
            "quantity": 1
        }
    ]
}

// What server controls
- price (from Product entity)
- total_amount (calculated)
- order_date (LocalDateTime.now())
- status (OrderStatus.PENDING)
- user (from Authentication)
```

#### Stock Management
**Choice:** Automatic inventory tracking
**Implementation:**
- Stock reduced when order created
- Stock restored when order updated/deleted
- Stock validation before order creation
- Uses `@Transactional` to ensure atomicity

**Rationale:**
- Prevents overselling
- Maintains data consistency
- Automatic rollback on errors

### 4. Database Design

#### Entity Relationships
```
User (1) ─────< (M) Order (1) ─────< (M) OrderItem (M) >───── (1) Product
User (M) >─────< (M) Role
```

**Key Decisions:**
1. **Order-OrderItem:** One-to-Many with `orphanRemoval = true`
    - Automatically deletes order items when order is deleted
    - Maintains referential integrity

2. **User-Role:** Many-to-Many
    - Users can have multiple roles (extensible)
    - Roles can be assigned to multiple users
    - Join table: `user_roles`

3. **OrderItem-Product:** Many-to-One
    - Multiple order items can reference same product
    - Product deletion doesn't cascade (business rule)

    
### 5. Authorization Model

#### Ownership-Based Access Control
**Implementation:** Orders are protected by both role and ownership

**Access Matrix:**

| Operation              | USER (Owner) | USER (Non-owner) | ADMIN |
|------------------------|--------------|---------------|-------|
| Create Order           | ✅ | ❌ | ✅ |
| View Own Order         | ✅ | ❌ | ✅ |
| View Others' Orders    | ❌ | ❌ | ✅ |
| Update Own Order Items | ✅ | ❌ | ✅ |
| Update Order Status    | ❌ | ❌ | ✅ |
| Delete Own Order       | ✅ | ❌ | ✅ |
| View All Orders        | ❌ | ❌ | ✅ |

**Implementation Pattern:**
```java
// Step 1: Role check at controller (@PreAuthorize)
// Step 2: Ownership check at service layer
private boolean isAdminOrOwner(Authentication auth, Order order) {
    return isAdmin(auth) || order.getUser().getUsername().equals(auth.getName());
}
```

### 6. DTO Pattern

#### Choice: Separate DTOs for Request and Response
**Request DTOs:** OrderCreateDto, ProductCreateDto, UserDto
**Response DTOs:** OrderResponseDto, ProductResponseDto

**Rationale:**
- **Security:** Request DTOs exclude sensitive fields users shouldn't set
- **Flexibility:** Response can include calculated/derived data
- **Validation:** Different validation rules for input vs output
- **API Evolution:** Can change internal structure without breaking API

**Example:**
```java
// OrderCreateDto - minimal user input
{
    "orderItems": [...]
}

// OrderResponseDto - complete server response
{
    "id": 1,
    "order_date": "2026-02-14T10:30:00",
    "status": "PENDING",
    "total_amount": 1899.99,
    "user_id": 2,
    "username": "john"
}
```

### 7. Error Handling

#### Custom Exceptions
**Implemented:**
- `ResourceNotFoundException` - 404 responses
- `AccessDeniedException` - 403 responses (Spring Security)
- `IllegalStateException` - 400 responses (e.g., insufficient stock)

**Design Choice:** Let Spring Security handle authentication errors (401)

### 8. Transaction Management

#### Choice: `@Transactional` on critical operations
**Applied to:**
- `createOrder()` - ensures order + items + stock update is atomic
- `updateOrder()` - ensures stock restoration and update is atomic
- `deleteOrder()` - ensures stock restoration happens

**Rationale:**
- All-or-nothing guarantee (ACID properties)
- Automatic rollback on exceptions
- Data consistency across multiple operations
- Prevents partial updates (e.g., order created but stock not updated)

---

## Assumptions Made

### 1. User Registration
**Assumption:** Users can self-register with any role (including ADMIN)
**Justification:** For testing and demo purposes. In production, ADMIN role assignment should be restricted.

### 2. Product Management
**Assumption:** Only ADMIN can create/update/delete products
**Justification:** Business rule - regular users are consumers, not sellers.

### 3. Order Status Flow
**Assumption:** Simple two-state model (PENDING, SHIPPED)
**Justification:** Simplified for scope. Real systems would have: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED

### 4. Stock Management
**Assumption:** No reserved/pending stock
**Justification:** Stock is immediately deducted when order is created. In production, might need a "reserved" state.

### 5. Price Precision
**Assumption:** `BigDecimal` with precision 10, scale 2
**Justification:** Sufficient for most currencies (up to $99,999,999.99). Avoids floating-point precision issues.

### 6. Authentication Credentials
**Assumption:** Credentials sent with every request (stateless)
**Justification:** RESTful API design. No sessions, no cookies.

### 7. Concurrent Order Processing
**Assumption:** Basic transaction isolation handles concurrency
**Justification:** For small-scale application. Large-scale would need optimistic locking or distributed locks.

### 8. Order Modification
**Assumption:** Users can modify their pending orders
**Business Rule:** Once ADMIN changes status to SHIPPED, users should not be able to modify (not fully implemented - future enhancement).

### 9. Product Deletion
**Assumption:** Products can be deleted even if referenced in orders
**Justification:** OrderItem stores `price_at_purchase` and product reference. Historical orders remain intact.

### 10. Date/Time Format
**Assumption:** Using `LocalDateTime` for order dates
**Justification:** Server timezone is used. Production might need ZonedDateTime for global applications.

---

## API Endpoints Summary

### Public Endpoints
- `POST /api/register` - User registration

### Product Endpoints (Authenticated)
- `GET /api/products` - Browse all products (Any user)
- `GET /api/products/{id}` - View product details (Any user)
- `POST /api/products/create` - Create product (ADMIN only)
- `PUT /api/products/{id}` - Update product (ADMIN only)
- `DELETE /api/products/{id}` - Delete product (ADMIN only)

### Order Endpoints (Authenticated)
- `POST /api/orders/create` - Create order (USER/ADMIN)
- `GET /api/orders` - List orders (Own orders for USER, all for ADMIN)
- `GET /api/orders/{id}` - View order (Owner or ADMIN)
- `PUT /api/orders/{id}` - Update order (Owner can update items, ADMIN can update status)
- `DELETE /api/orders/{id}` - Delete order (Owner or ADMIN)

---

## Testing Considerations

### Test Users Created
1. **ADMIN User:**
    - Username: admin
    - Password: admin123
    - Can manage products and view all orders

2. **Regular Users:**
    - John (john/john123)
    - Jane (jane/jane123)
    - Can create orders and manage their own orders

### Test Workflow
1. Register users (ADMIN, USER)
2. Login as ADMIN, create products
3. Login as USER, browse products
4. Create order (server calculates prices)
5. View orders (users see only their own)
6. Login as ADMIN, view all orders
7. ADMIN updates order status to SHIPPED
8. Test authorization failures (403 responses)

---

## Known Limitations & Future Enhancements

### Current Limitations
1. **No payment integration** - orders are created but not paid
2. **Simple status flow** - only PENDING and SHIPPED
3. **No order history** - updates overwrite, no audit trail
4. **No pagination** - GET all orders/products returns everything
5. **Basic stock management** - no reserved stock during checkout process
6. **No product categories** - all products in flat list
7. **No cart functionality** - orders created directly
8. **No email notifications** - no confirmation emails
9. **HTTP Basic Auth** - not suitable for browser-based clients

### Recommended Enhancements
1. Implement JWT authentication for better client support
2. Add pagination and filtering to list endpoints
3. Implement product categories and search
4. Add shopping cart functionality
5. Implement order status workflow (PROCESSING, DELIVERED, CANCELLED)
6. Add audit logging for order changes
7. Implement stock reservation during checkout
8. Add payment gateway integration
9. Email notifications for order confirmations
10. Add product reviews and ratings
11. Implement CORS configuration for frontend integration
12. Add API rate limiting
13. Implement soft delete for products/orders

---

## Security Best Practices Implemented

1. ✅ Passwords hashed with BCrypt
2. ✅ SQL injection prevention (JPA/Hibernate)
3. ✅ CSRF disabled (stateless API)
4. ✅ Role-based access control
5. ✅ Ownership validation for resources
6. ✅ Server-side price calculation (prevent manipulation)
7. ✅ Input validation at DTO level
8. ✅ Exception handling (no sensitive data in errors)
9. ✅ Transactional integrity
10. ✅ Principle of least privilege (users can only access their own data)

---

## Database Schema Notes

### Tables Created
1. **users** - User accounts
2. **roles** - Available roles (ADMIN, USER)
3. **user_roles** - User-Role mapping (many-to-many)
4. **products** - Product catalog
5. **orders** - Customer orders
6. **order_items** - Individual items in orders

### Relationships
- User → Order: One-to-Many
- Order → OrderItem: One-to-Many (cascade all, orphan removal)
- OrderItem → Product: Many-to-One
- User → Role: Many-to-Many

---

## Configuration Requirements

### application.properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security
spring.security.user.name=admin
spring.security.user.password=admin123
```

---

## Conclusion

This e-commerce API demonstrates:
- Secure authentication and authorization
- RESTful API design principles
- Proper separation of concerns (DTO, Service, Repository pattern)
- Database relationship management
- Transaction management
- Security-first approach (server-side validation and calculation)
- Role-based and ownership-based access control

The design prioritizes security and data integrity while maintaining simplicity and clarity for educational purposes.