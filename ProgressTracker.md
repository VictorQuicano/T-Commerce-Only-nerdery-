# 📊 Project Progress Tracker — TCommerce API Store

---

## 🏗️ Technical Requirements

| Requirement | Status | Notes |
|---|---|---|
| PostgreSQL database | ✅ Done | Configured in `.env` and `application.properties` |
| Hibernate ORM (Spring Data JPA) | ✅ Done | Full JPA entity layer in place |
| Spring Boot framework | ✅ Done | Spring Boot 3.4.2 |
| Global exception filter | ✅ Done | `GlobalExceptionHandler.java` with `ProblemDetail` |
| Bean validations (`@Valid`, `@NotNull`, etc.) | ✅ Done | Used across all request DTOs |
| Custom annotations | ✅ Done | `@StrongPassword`, `@ValidEmail`, `@ExistEmail`, `@ValidImage`, `@MaxFileSize`, `@ValidImageList` |
| CSRF / HTTP exploit prevention | ✅ Done | CSRF disabled (stateless JWT), security config in place |
| CORS configuration | ✅ Done | `SecurityConfiguration.java` and `spring.graphql.cors` config |
| Rate limiting on password reset | ✅ Done | `PasswordResetRateLimit` entity + service logic |
| Schema validation for env variables | ⚠️ Partial | Used in `BucketConfig` via `@ConfigurationProperties`, but missing `@Validated` for strict startup validation. |

---

## ✅ Mandatory Feature Progress

### 1. Authentication System (REST)

| Endpoint / Feature | Status | Notes |
|---|---|---|
| `POST /sign-up` | ✅ Done | `AuthController` |
| `POST /sign-in` | ✅ Done | JWT + refresh token cookies |
| `POST /logout` | ✅ Done | Clears JWT & refresh token cookies |
| `POST /forgot-password` | ✅ Done | Rate-limited, sends email via `MailEventPublisher` |
| `POST /reset-password` | ✅ Done | Validates token and updates password |
| Secure password hashing | ✅ Done | BCrypt via `PasswordEncoder` |
| JWT authentication | ✅ Done | `JwtAuthenticationFilter` |
| Refresh tokens | ✅ Done | `RefreshTokenService` |
| Password reset token expiry | ✅ Done | 24-hour expiry |
| Rate limiting on reset | ✅ Done | 5 attempts, 20-min block |

---

### 2. Product Catalog (REST)

| Feature | Status | Notes |
|---|---|---|
| List products with pagination | ✅ Done | Cursor-based keyset pagination (`Window` API) |
| Page metadata (total, from, to) | ✅ Done | `PaginatedResponse` DTO |
| Sorting (price, name, date) | ✅ Done | Dynamic `SortBy` support |
| Filter by category | ✅ Done | `categoryId` query param |
| Filter by name | ✅ Done | `name` query param |
| Get product by ID | ✅ Done | `GET /products/{id}` |
| Public access (no auth required) | ✅ Done | Permitted in `SecurityConfiguration` |

---

### 3. User Roles

| Feature | Status | Notes |
|---|---|---|
| `MANAGER` role | ✅ Done | `ERole.MANAGER` enum, `@PreAuthorize("hasRole('MANAGER')")` |
| `CLIENT` role | ✅ Done | `ERole.CLIENT` (default on registration) |
| Role-based endpoint protection | ✅ Done | `@PreAuthorize` on Manager/Client controllers and GraphQL resolvers |
| 403 Forbidden for unauthorized access | ✅ Done | `CustomAccessDeniedHandler` |

---

### 4. Email Notification System

| Feature | Status    | Notes |
|---|-----------|---|
| Async email processing | ✅ Done    | `AsyncConfig` + `@Async` via `MailListenerService` |
| Multiple delivery channels | ✅ Done    | `SmtpMailSender`, `RestApiSendgridMailService`, `RestApiResendMailService`, `MockMailService` |
| Template-based emails | ✅ Done    | SendGrid dynamic templates and Resend HTML body generation |
| Password reset email | ✅ Done    | Triggered in `PasswordResetServiceImpl` |
| Password change confirmation email | ✅ Done    | Triggered after successful password reset |
| Order status updates email | ❌ Missing | `OrderService.updateOrderStatus()` does **not** publish an `EmailEvent` yet |
| Low stock alerts (liked products) | ✅ Done    | Requirement: stock < 3 units for liked items |
| Discount alerts (liked products) | ✅ Done    | No listener for price changes |

---

### 5. Manager Capabilities

| Feature | Status | Notes |
|---|---|---|
| Create products | ✅ Done | `POST /manager/products` |
| Update products (PATCH/PUT) | ✅ Done | `PATCH /manager/products/{id}` |
| Delete products (soft delete) | ✅ Done | `DELETE /manager/products/{id}` (REST) and `deleteProduct` (GraphQL) |
| Disable products | ✅ Done | `isActive` flag in `Product` entity; `disableProduct` in GraphQL mutation |
| Upload product images | ✅ Done | `POST /manager/products/{id}/images` (GCP Storage) |
| View client orders with pagination | ✅ Done | `GET /manager/orders` (REST) and `managerOrders` (GraphQL) with cursor pagination |
| Update delivery status | ✅ Done | `updateDeliveryStatus` in GraphQL mutation; validates role |
| Price change logging | ❌ Missing | No logic to log product price history |
| Prevent deletion if product has pending orders |  ✅ Done    | `deleteProduct()` needs to check for active orders |

---

### 6. Client Capabilities

| Feature | Status | Notes |
|---|---|---|
| View products (public) | ✅ Done | `GET /products` |
| View product details | ✅ Done | `GET /products/{id}` with images, category, stock |
| "Out of Stock" indication | ✅ Done | Stock quantity included in response; handled by frontend |
| Add to cart | ✅ Done | `POST /cart/items` (REST) and `addToCart` (GraphQL) |
| Remove from cart | ✅ Done | `DELETE /cart/items/{productId}` (REST) and `removeFromCart` (GraphQL) |
| View cart | ✅ Done | `GET /cart` |
| Clear cart | ✅ Done | `DELETE /cart` (REST) and `clearCart` (GraphQL) |
| Like product | ✅ Done | `toggleLike` in GraphQL (mutation) and REST controllers |
| Buy products (create order) | ✅ Done | `POST /orders` (REST) and `createOrderFromCart` (GraphQL) |
| View my orders with pagination | ✅ Done | `GET /orders` (REST) and `myOrders` (GraphQL) with cursor pagination |
| Track order status | ✅ Done | `OrderStatusHistory` stores history; `GET /orders/{id}` |
| Stock reduction on purchase |  ✅ Done    | Order creation does not yet atomically decrement stock |
| Guest cart | ❌ Missing | Currently requires authentication |

---

### 7. Public Product Visibility

| Feature | Status | Notes |
|---|---|---|
| Products accessible without auth | ✅ Done | `/api/v1/products/**` is in permit list |
| Details accessible without auth | ✅ Done | Same as listing |

---

### 8. Stripe Payment Integration

| Feature | Status | Notes |
|---|---|---|
| Create Payment Intent on checkout | ✅ Done | `POST /payments/checkout` |
| Webhook handling | ✅ Done | `POST /api/v1/payments/webhook` |
| Stripe signature verification | ✅ Done | `Webhook.constructEvent()` |
| Idempotent webhook handling | ✅ Done | `ProcessedStripeEvent` deduplication |
| Update order status via webhook | ✅ Done | Success/Failure handled |

---
## 📋 API Protocol Requirements

| Requirement | Status | Notes |
|---|---|---|
| REST: Auth endpoints | ✅ Done | Full REST auth layer |
| REST: Product CRUD | ✅ Done | Manager controllers |
| REST: Product listing & search | ✅ Done | Public product controller |
| GraphQL: Order management | ✅ Done | `managerOrders`, `myOrders`, `createOrderFromCart`, `updateDeliveryStatus` |
| GraphQL: Cart management | ✅ Done | `addToCart`, `removeFromCart`, `clearCart` |
| GraphQL: Manager Features | ✅ Done | `deleteProduct`, `disableProduct` |
| GraphQL: Client Features | ✅ Done | `toggleLike` |

---

## 🚨 Critical Missing Items (Priority Order)

| Priority | Item | Impact |
|---|---|---|
| 🔴 HIGH | **Stock decrement on order creation** | Data integrity/overselling risk |
| 🟠 MED | **Email: Order status change notification** | Core UX requirement |
| 🟠 MED | **Email: Low stock alert for liked products** | Marketing/Engagement requirement |
| 🟠 MED | **Email: Discount alert for liked products** | Marketing/Engagement requirement |
| 🟡 LOW | **Startup env variable validation** | `@Validated` in `@ConfigurationProperties` |
| 🟡 LOW | **Prevent product deletion with active orders** | Referential integrity |
| 🟡 LOW | **Price change logging** | Audit requirement |
