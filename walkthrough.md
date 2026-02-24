# 📊 Project Progress Tracker — TCommerce API Store

> Auto-generated against `Requirements.md`. Last updated: 2026-02-20

---

## 🏗️ Technical Requirements

| Requirement | Status | Notes |
|---|---|---|
| PostgreSQL database | ✅ Done | Configured in `application-prod.properties` |
| Hibernate ORM (Spring Data JPA) | ✅ Done | Full JPA entity layer in place |
| Spring Boot framework | ✅ Done | Spring Boot 3.4.2 |
| Global exception filter | ✅ Done | `GlobalExceptionHandler.java` with `ProblemDetail` |
| Bean validations (`@Valid`, `@NotNull`, etc.) | ✅ Done | Used across all request DTOs |
| Custom annotations | ✅ Done | `@StrongPassword`, `@ValidEmail`, `@ExistEmail`, `@ValidImage`, `@MaxFileSize` |
| CSRF / HTTP exploit prevention | ✅ Done | CSRF disabled (stateless JWT), security config in place |
| CORS configuration | ✅ Done | `SecurityConfiguration.java` CORS filter |
| Rate limiting on password reset | ✅ Done | `PasswordResetRateLimit` entity + service logic |
| Schema validation for env variables | ⚠️ Partial | `application-dev/prod.properties` exist but no `@Validated @ConfigurationProperties` startup validation |

---

## ✅ Mandatory Feature Progress

### 1. Authentication System (REST)

| Endpoint / Feature | Status | Notes |
|---|---|---|
| `POST /sign-up` | ✅ Done | `AuthController` |
| `POST /sign-in` | ✅ Done | JWT + refresh token cookies |
| `POST /logout` | ✅ Done | Clears JWT & refresh token cookies |
| `POST /reset-password` (forgot) | ✅ Done | Rate-limited, sends email via `MailEventPublisher` |
| `PUT /reset-password/{token}` | ✅ Done | Token expiry enforced |
| Secure password hashing | ✅ Done | BCrypt via `PasswordEncoder` |
| JWT authentication | ✅ Done | `JwtAuthenticationFilter` |
| Refresh tokens | ✅ Done | `RefreshTokenService` |
| Password reset token expiry | ✅ Done | 24-hour expiry |
| Rate limiting on reset | ✅ Done | 20 attempts, 20-min block |

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
| `MANAGER` role | ✅ Done | `ERole.MANAGER` enum, `@PreAuthorize("hasAnyRole('MANAGER')")` |
| `CLIENT` role | ✅ Done | `ERole.CLIENT` (default on registration) |
| Role-based endpoint protection | ✅ Done | `@PreAuthorize` on Manager controllers |
| 403 Forbidden for unauthorized access | ✅ Done | `CustomAccessDeniedHandler` |

---

### 4. Email Notification System

| Feature | Status    | Notes |
|---|-----------|---|
| Async email processing | ✅ Done    | `AsyncConfig` + `@Async` via `MailListenerService` |
| Multiple delivery channels (SMTP, REST, Mock) | ✅ Done    | `SmtpMailSender`, `RestApiMailService` (SendGrid), `MockMailService` |
| Template-based emails (SendGrid dynamic templates) | ✅ Done    | `RestApiMailService` uses SendGrid template ID |
| Password reset email | ✅ Done    | Triggered in `PasswordResetServiceImpl` |
| Order status update email | ❌ Missing | `OrderService.updateOrderStatus()` does **not** publish an `EmailEvent` |
| Low stock alert (liked products) | ❌ Missing | No listener or trigger exists |
| Discount alert (liked products) | ❌ Missing | No listener or trigger exists |
| Password change confirmation email | ✅ Done    | `resetPassword()` does not send a confirmation email |

---

### 5. Manager Capabilities

| Feature | Status | Notes |
|---|---|---|
| Create products | ✅ Done | `POST /manager/products` |
| Update products (PATCH) | ✅ Done | `PATCH /manager/products/{id}` |
| Delete products (soft delete) | ✅ Done | `DELETE /manager/products/{id}` |
| Upload product images | ✅ Done | `POST /manager/products/{id}/images` (GCP Storage) |
| Remove product images | ✅ Done | `DELETE /manager/products/{id}/images/{imageId}` |
| **Disable products** | ❌ Missing | No `enabled`/`disabled` flag on `Product` entity; no endpoint |
| **View client orders with pagination** | ❌ Missing | `OrderController` only returns current user's orders; no manager-scoped order list |
| **Update delivery status** | ❌ Missing | No endpoint to update order status as manager; no status-transition validation |
| Price change logging | ⚠️ Partial | `OrderStatusHistory` exists for orders, but no price-change log for products |
| Prevent deletion if product has pending orders | ❌ Missing | `deleteProduct()` does not check active orders |

---

### 6. Client Capabilities

| Feature | Status | Notes |
|---|---|---|
| View products (public) | ✅ Done | `GET /products` |
| View product details | ✅ Done | `GET /products/{id}` with images, category, stock |
| "Out of Stock" indication | ⚠️ Partial | `StockEntity` exists, but response doesn't explicitly flag `isOutOfStock` |
| Add to cart | ✅ Done | `POST /cart/items` |
| Remove from cart | ✅ Done | `DELETE /cart/items/{productId}` |
| View cart | ✅ Done | `GET /cart` |
| Clear cart | ✅ Done | `DELETE /cart` |
| Like product | ✅ Done | `POST /products/{productId}/like` (auth required) |
| Unlike product | ✅ Done | `DELETE /products/{productId}/like` (auth required) |
| Buy products (create order) | ✅ Done | `POST /orders` — creates order from cart, clears cart |
| View my orders | ✅ Done | `GET /orders` |
| View order by ID | ✅ Done | `GET /orders/{orderId}` with ownership check |
| **Orders with pagination** | ❌ Missing | `getUserOrders()` returns all orders with no pagination |
| Stock reduction on purchase | ⚠️ Partial | `CartService` validates stock; order creation doesn't atomically decrement stock |
| Guest cart | ❌ Missing | Cart requires authentication; no session-based guest cart |
| Track order status | ✅ Done | `OrderStatusHistory` entity records all transitions |

---

### 7. Public Product Visibility

| Feature | Status | Notes |
|---|---|---|
| Products accessible without auth | ✅ Done | `/api/v1/products/**` is in permit list |
| Images accessible without auth | ✅ Done | GCP Storage public URLs in product response |

---

### 8. Stripe Payment Integration

| Feature | Status | Notes |
|---|---|---|
| Create Payment Intent on checkout | ✅ Done | `POST /payments/checkout` |
| Return `clientSecret` to frontend | ✅ Done | `CheckoutResponse { clientSecret }` |
| Webhook: `payment_intent.succeeded` → `PAID` | ✅ Done | `handlePaymentSuccess()` |
| Webhook: `payment_intent.payment_failed` → `PAYMENT_FAILED` | ✅ Done | `handlePaymentFailure()` |
| Stripe signature verification | ✅ Done | `Webhook.constructEvent()` |
| Never store card details | ✅ Done | Only `paymentIntentId` stored |
| Idempotent webhook handling | ✅ Done | `ProcessedStripeEvent` table |
| Order status update via webhook | ✅ Done | Uses existing `updateOrderStatus()` |

---

## 📋 API Protocol Requirements

| Requirement | Status | Notes |
|---|---|---|
| REST: Auth endpoints | ✅ Done | Full REST auth layer |
| REST: Product CRUD | ✅ Done | Manager controllers |
| REST: Product listing & search | ✅ Done | Public product controller |
| **GraphQL: Order management** | ❌ Missing | No GraphQL setup at all |
| **GraphQL: Cart management** | ❌ Missing | Cart is REST only |
| **GraphQL: Delete/disable products (Manager)** | ❌ Missing | Manager ops are REST only |
| **GraphQL: Like products (Client)** | ❌ Missing | Like is REST, not GraphQL |

---

## 🎁 Optional Features

| Feature | Status |
|---|---|
| GraphQL Resolve Fields | ❌ Not started |
| Deployment | ❌ Not started |
| Refund & Return System | ❌ Not started |
| Auction System | ❌ Not started |
| Recommendation System | ❌ Not started |

---

## 🚨 Critical Missing Items (Priority Order)

| Priority | Item | Impact |
|---|---|---|
| 🔴 HIGH | **GraphQL** — Order, Cart, Like, Manager ops | Core protocol requirement |
| 🔴 HIGH | **Manager: View all orders with pagination** | Manager oversight capability |
| 🔴 HIGH | **Manager: Update delivery status** | Core manager feature |
| 🔴 HIGH | **Manager: Disable/enable products** | Core manager feature |
| 🟠 MED | **Email: Order status change notification** | Triggered on every status update |
| 🟠 MED | **Email: Low stock alert for liked products** | Requires stock change listener |
| 🟠 MED | **Email: Discount alert for liked products** | Requires price change listener |
| 🟠 MED | **Email: Password change confirmation** | After `resetPassword()` |
| 🟡 LOW | **Orders pagination** (client view) | UX improvement |
| 🟡 LOW | **Stock decrement on order creation** | Concurrency/integrity issue |
| 🟡 LOW | **Startup env variable validation** | `@ConfigurationProperties` + `@Validated` |
| 🟡 LOW | **Prevent product deletion with active orders** | Data integrity |
| 🟡 LOW | **Explicit `isOutOfStock` flag in product response** | UX clarity |

---

## 📈 Progress Summary

| Area | Done | Total | % |
|---|---|---|---|
| Technical requirements | 9 | 10 | 90% |
| Auth system | 10 | 10 | 100% |
| Product catalog | 7 | 7 | 100% |
| User roles | 4 | 4 | 100% |
| Email notifications | 3 | 7 | 43% |
| Manager capabilities | 5 | 8 | 63% |
| Client capabilities | 11 | 14 | 79% |
| Public visibility | 2 | 2 | 100% |
| Stripe payments | 8 | 8 | 100% |
| **GraphQL protocol** | **0** | **4** | **0%** |
| **Overall** | **~59** | **~74** | **~80%** |
