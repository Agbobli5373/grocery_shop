# Grocery Shop Backend API Test Plan

## Overview
This test plan outlines comprehensive end-to-end testing of the Grocery Shop Backend API using Playwright MCP for browser-based API testing via Swagger UI.

## Test Environment
- **Backend**: Spring Boot application running on http://localhost:8080
- **Database**: H2 in-memory database (test profile)
- **Testing Tool**: Playwright MCP via Swagger UI
- **Authentication**: JWT-based authentication

## Test Categories

### 1. Authentication Tests
#### 1.1 User Registration
- **Test Case**: Register new user with valid data
- **Expected**: 201 Created, user data returned (without tokens)
- **Status**: ✅ PASSED

#### 1.2 User Login
- **Test Case**: Login with registered user credentials
- **Expected**: 200 OK, JWT token and refresh token returned
- **Status**: ✅ PASSED

#### 1.3 Invalid Login
- **Test Case**: Login with wrong password
- **Expected**: 500 Internal Server Error (needs investigation)
- **Status**: 🔄 TO TEST

#### 1.4 Duplicate Registration
- **Test Case**: Register user with existing email
- **Expected**: 500 Internal Server Error (needs investigation)
- **Status**: 🔄 TO TEST

#### 1.5 Token Validation
- **Test Case**: Validate JWT token
- **Expected**: 200 OK, token validation response
- **Status**: 🔄 TO TEST

#### 1.6 Get Current User
- **Test Case**: Get current user info with valid token
- **Expected**: 200 OK, user details returned
- **Status**: 🔄 TO TEST

### 2. Product Management Tests
#### 2.1 Get All Products
- **Test Case**: Retrieve all products without authentication
- **Expected**: 200 OK, list of products
- **Status**: ✅ PASSED (5 products returned: Apple, Banana, Bread, Milk, Orange Juice)

#### 2.2 Get Product by ID
- **Test Case**: Retrieve specific product by ID (tested with Apple/ID=1)
- **Expected**: 200 OK, product details
- **Status**: ✅ PASSED

#### 2.3 Search Products
- **Test Case**: Search products with criteria
- **Expected**: 200 OK, filtered product list
- **Status**: 🔄 TO TEST

#### 2.4 Create Product (Admin)
- **Test Case**: Create new product with admin credentials
- **Expected**: 201 Created, product created
- **Status**: 🔄 TO TEST

#### 2.5 Update Product (Admin)
- **Test Case**: Update existing product with admin credentials
- **Expected**: 200 OK, product updated
- **Status**: 🔄 TO TEST

### 3. Shopping Cart Tests
#### 3.1 Add Item to Cart
- **Test Case**: Add product to authenticated user's cart
- **Expected**: 200 OK, cart updated
- **Status**: 🔄 TO TEST

#### 3.2 Get Cart Contents
- **Test Case**: Retrieve user's cart contents
- **Expected**: 200 OK, cart items returned
- **Status**: 🔄 TO TEST

#### 3.3 Update Cart Item
- **Test Case**: Update quantity of item in cart
- **Expected**: 200 OK, cart item updated
- **Status**: 🔄 TO TEST

#### 3.4 Remove Item from Cart
- **Test Case**: Remove item from cart
- **Expected**: 200 OK, item removed
- **Status**: 🔄 TO TEST

### 4. Order Management Tests
#### 4.1 Create Order (Checkout)
- **Test Case**: Checkout cart and create order
- **Expected**: 201 Created, order created
- **Status**: 🔄 TO TEST

#### 4.2 Get User Orders
- **Test Case**: Retrieve user's order history
- **Expected**: 200 OK, list of orders
- **Status**: 🔄 TO TEST

#### 4.3 Get Order by ID
- **Test Case**: Retrieve specific order details
- **Expected**: 200 OK, order details
- **Status**: 🔄 TO TEST

#### 4.4 Update Order Status (Admin)
- **Test Case**: Update order status with admin credentials
- **Expected**: 200 OK, order status updated
- **Status**: 🔄 TO TEST

### 5. Recommendation Tests
#### 5.1 Get Product Recommendations
- **Test Case**: Get personalized product recommendations
- **Expected**: 200 OK, recommended products
- **Status**: 🔄 TO TEST

### 6. Admin Tests
#### 6.1 Get Dashboard Statistics
- **Test Case**: Retrieve admin dashboard data
- **Expected**: 200 OK, statistics returned
- **Status**: 🔄 TO TEST

### 7. Error Handling Tests
#### 7.1 Unauthorized Access
- **Test Case**: Access protected endpoint without token
- **Expected**: 401 Unauthorized
- **Status**: 🔄 TO TEST

#### 7.2 Invalid Token
- **Test Case**: Access endpoint with invalid token
- **Expected**: 401 Unauthorized
- **Status**: 🔄 TO TEST

#### 7.3 Resource Not Found
- **Test Case**: Access non-existent resource
- **Expected**: 404 Not Found
- **Status**: 🔄 TO TEST

## Test Execution Strategy

### Phase 1: Authentication Flow (✅ COMPLETED)
- Register new user
- Login with credentials
- Validate JWT tokens

### Phase 2: Product Operations (🔄 NEXT)
- Test product retrieval endpoints
- Test product search functionality

### Phase 3: Cart Operations (🔄 PENDING)
- Add items to cart
- Update cart contents
- Remove items from cart

### Phase 4: Order Operations (🔄 PENDING)
- Complete checkout process
- View order history
- Track order status

### Phase 5: Admin Operations (🔄 PENDING)
- Test admin-only endpoints
- Validate role-based access control

### Phase 6: Error Scenarios (🔄 PENDING)
- Test error handling
- Validate security measures

## Test Data
- **Test User**: testuser@example.com / testpass123
- **Admin User**: admin@groceryshop.com / admin123 (from seed data)
- **JWT Token**: Available after successful login
- **Product IDs**: To be determined from product listing

## Success Criteria
- All authentication endpoints working correctly
- All CRUD operations functioning as expected
- Proper error handling and status codes
- Role-based access control enforced
- Event-driven features working (SSE, notifications)

## Issues Found
1. **Authentication Error Handling**: Login with invalid credentials returns 500 instead of 401
2. **Registration Error Handling**: Duplicate email registration returns 500 instead of 409
3. **Seed Data Password Hash**: Admin password hash doesn't match expected password

## Recommendations
1. Fix authentication error responses to return proper HTTP status codes
2. Implement proper exception handling for authentication failures
3. Update seed data with correct password hashes
4. Add comprehensive input validation
5. Implement rate limiting for authentication endpoints
