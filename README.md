# TCommerce API 🛒

A modern, high-performance REST API for E-commerce built with **Spring Boot 3.4**, featuring Stripe integration, automated email notifications, and a clean, layered architecture.

---

## 🏗️ Architecture & Design Patterns

The project follows a **Layered Hexagonal Architecture** (Clean Architecture principles), ensuring high maintainability, testability, and separation of concerns.

### Project Layers:
- **Domain Layer**: Contains the pure business logic, entities, and repository interfaces. It is independent of external frameworks.
- **Application Layer**: Orchestrates business logic via services, handles DTO mapping, and exposes REST controllers.
- **Infrastructure Layer**: Implementation of external concerns like database persistence (JPA), security (Spring Security + JWT), and third-party integrations (Stripe, Resend, GCP).
- **Interfaces Layer**: Defines the Data Transfer Objects (DTOs) and validation logic for external communication.

### Design Patterns Used:
- **Repository Pattern**: Decouples the domain layer from the data access logic.
- **Service Layer Pattern**: Encapsulates business logic and coordinates application activities.
- **Data Transfer Object (DTO)**: Standardizes data exchange between the API and clients.
- **Mapper Pattern**: Manages the conversion between Domain Entities and JPA Entities/DTOs.
- **Builder Pattern (Lombok)**: Used for clean and readable object instantiation.
- **Dependency Injection**: Facilitates loose coupling between components.
- **Observer Pattern**: Handles asynchronous events like mail notifications.

---

## 📁 Folder Structure

```
com.tcommerce.TCommerce
├── application
│   ├── controllers
│   │   ├── ApiPaths.java
│   │   ├── auth
│   │   │   ├── AuthController.java
│   │   │   └── PasswordResetController.java
│   │   ├── commerce
│   │   │   ├── CategoryController.java
│   │   │   ├── ProductController.java
│   │   │   └── ProductLikeController.java
│   │   ├── manager
│   │   │   ├── ManagerCategoryController.java
│   │   │   ├── ManagerOrderController.java
│   │   │   └── ManagerProductController.java
│   │   └── sales
│   │       ├── CartController.java
│   │       ├── OrderController.java
│   │       └── PaymentController.java
│   ├── enums
│   │   └── TokenType.java
│   ├── query
│   │   ├── OrderFilter.java
│   │   ├── OrderPaginationRequest.java
│   │   ├── ProductFilter.java
│   │   └── ProductPaginationRequest.java
│   ├── seeders
│   │   └── DataSeeder.java
│   └── services
│       ├── auth
│       │   ├── AuthService.java
│       │   ├── impl
│       │   │   ├── AuthServiceImpl.java
│       │   │   ├── JwtServiceImpl.java
│       │   │   ├── PasswordResetServiceImpl.java
│       │   │   └── RefreshTokenServiceImpl.java
│       │   ├── JwtService.java
│       │   ├── PasswordResetService.java
│       │   └── RefreshTokenService.java
│       ├── commerce
│       │   ├── CategoryService.java
│       │   ├── ProductImageService.java
│       │   ├── ProductLikeService.java
│       │   ├── ProductService.java
│       │   └── StockNotificationService.java
│       ├── common
│       │   ├── EmailGenerator.java
│       │   ├── HtmlBodyGenerator.java
│       │   ├── PageProcessor.java
│       │   └── ParseBodyService.java
│       └── sales
│           ├── CartService.java
│           ├── ChangeStatusNotificationService.java
│           ├── OrderCleanupService.java
│           ├── OrderService.java
│           ├── PaymentService.java
│           ├── RefundNotification.java
│           └── ShippingService.java
├── config
│   ├── ApplicationConfig.java
│   ├── AsyncConfig.java
│   ├── BucketConfig.java
│   ├── CustomAccessDeniedHandler.java
│   ├── Http401UnauthorizedEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   ├── OpenApiConfig.java
│   ├── SecurityConfiguration.java
│   └── StripeConfig.java
├── domain
│   ├── entities
│   │   ├── auth
│   │   │   ├── PasswordResetRateLimit.java
│   │   │   ├── PasswordResetToken.java
│   │   │   ├── RefreshToken.java
│   │   │   └── User.java
│   │   ├── BaseEntity.java
│   │   ├── commerce
│   │   │   ├── Category.java
│   │   │   ├── ProductImage.java
│   │   │   ├── Product.java
│   │   │   ├── ProductPriceHistory.java
│   │   │   ├── Stock.java
│   │   │   └── StockLevel.java
│   │   └── sales
│   │       ├── CartItem.java
│   │       ├── Cart.java
│   │       ├── OrderItem.java
│   │       ├── Order.java
│   │       ├── OrderStatusHistory.java
│   │       ├── OrderStatus.java
│   │       ├── ProcessedStripeEvent.java
│   │       └── Refund.java
│   ├── events
│   │   └── EmailEvent.java
│   ├── exceptions
│   │   ├── AlreadyExistsException.java
│   │   ├── CartEmptyException.java
│   │   ├── CrudErrorMessages.java
│   │   ├── CrudExceptionFactory.java
│   │   ├── CrudException.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── MailSendException.java
│   │   └── TokenException.java
│   ├── handlers
│   │   ├── ErrorResponse.java
│   │   └── TokenControllerHandler.java
│   ├── models
│   │   ├── PageInfo.java
│   │   ├── PaginatedResult.java
│   │   └── PaginationCriteria.java
│   ├── repositories
│   │   ├── implementations
│   │   │   ├── auth
│   │   │   │   ├── PasswordResetRateLimitRepositoryImpl.java
│   │   │   │   ├── PasswordResetTokenRepositoryImpl.java
│   │   │   │   ├── RefreshTokenRepositoryImpl.java
│   │   │   │   └── UserRepositoryImpl.java
│   │   │   ├── commerce
│   │   │   │   ├── CategoryRepositoryImpl.java
│   │   │   │   ├── ProductImageRepositoryImpl.java
│   │   │   │   └── ProductRepositoryImpl.java
│   │   │   ├── CRUDRepositoryImpl.java
│   │   │   └── sales
│   │   │       └── ProcessedStripeEventRepositoryImpl.java
│   │   └── interfaces
│   │       ├── auth
│   │       │   ├── PasswordResetRateLimitRepository.java
│   │       │   ├── PasswordResetTokenRepository.java
│   │       │   ├── RefreshTokenRepository.java
│   │       │   └── UserRepository.java
│   │       ├── commerce
│   │       │   ├── CategoryRepository.java
│   │       │   ├── ProductImageRepository.java
│   │       │   ├── ProductPriceHistoryRepository.java
│   │       │   └── ProductRepository.java
│   │       ├── CRUDRepository.java
│   │       └── sales
│   │           ├── CartRepository.java
│   │           ├── OrderRepository.java
│   │           ├── ProcessedStripeEventRepository.java
│   │           └── RefundRepository.java
│   └── services
│       ├── commerce
│       │   ├── StockAlertService.java
│       │   ├── StockLevelEvaluator.java
│       │   ├── StockNotifier.java
│       │   └── StockUpdater.java
│       ├── mail
│       │   ├── MailEventPublisher.java
│       │   ├── MailListenerService.java
│       │   └── MailService.java
│       └── StorageService.java
├── graphql
│   ├── mapper
│   │   └── GraphQLMapper.java
│   ├── resolver
│   │   ├── CartMutationResolver.java
│   │   ├── LikeMutationResolver.java
│   │   ├── OrderMutationResolver.java
│   │   ├── OrderQueryResolver.java
│   │   └── ProductMutationResolver.java
│   └── util
│       └── CursorUtil.java
├── infrastructure
│   ├── persistence
│   │   ├── entities
│   │   │   ├── auth
│   │   │   │   ├── ERole.java
│   │   │   │   ├── PasswordResetRateLimitEntity.java
│   │   │   │   ├── RefreshTokenEntity.java
│   │   │   │   └── UserEntity.java
│   │   │   ├── BaseEntity.java
│   │   │   ├── commerce
│   │   │   │   ├── CategoryEntity.java
│   │   │   │   ├── ProductEntity.java
│   │   │   │   ├── ProductImageEntity.java
│   │   │   │   ├── ProductLikeEntity.java
│   │   │   │   ├── ProductPriceHistoryEntity.java
│   │   │   │   └── StockEntity.java
│   │   │   └── sales
│   │   │       ├── CartEntity.java
│   │   │       ├── CartItemEntity.java
│   │   │       ├── OrderEntity.java
│   │   │       ├── OrderItemEntity.java
│   │   │       ├── OrderStatusHistoryEntity.java
│   │   │       ├── ProcessedStripeEventEntity.java
│   │   │       └── RefundEntity.java
│   │   ├── mappers
│   │   │   ├── auth
│   │   │   │   ├── PasswordResetRateLimitMapper.java
│   │   │   │   ├── RefreshTokenMapper.java
│   │   │   │   └── UserMapper.java
│   │   │   ├── commerce
│   │   │   │   ├── CategoryMapper.java
│   │   │   │   ├── ProductImageMapper.java
│   │   │   │   └── ProductMapper.java
│   │   │   └── sales
│   │   │       ├── CartMapper.java
│   │   │       ├── OrderMapper.java
│   │   │       ├── ProcessedStripeEventMapper.java
│   │   │       └── RefundMapper.java
│   │   ├── repositories
│   │   │   ├── auth
│   │   │   │   ├── JpaPasswordResetRateLimitRepository.java
│   │   │   │   └── JpaUserRepository.java
│   │   │   ├── commerce
│   │   │   │   ├── jpa
│   │   │   │   │   └── JpaProductPriceHistoryRepository.java
│   │   │   │   ├── JpaCategoryRepository.java
│   │   │   │   ├── JpaProductImageRepository.java
│   │   │   │   ├── JpaProductLikeRepository.java
│   │   │   │   ├── JpaProductRepository.java
│   │   │   │   ├── JpaRefreshTokenRepository.java
│   │   │   │   └── ProductPriceHistoryRepositoryImpl.java
│   │   │   └── sales
│   │   │       ├── CartRepositoryImpl.java
│   │   │       ├── JpaCartRepository.java
│   │   │       ├── JpaOrderRepository.java
│   │   │       ├── JpaProcessedStripeEventRepository.java
│   │   │       ├── JpaRefundRepository.java
│   │   │       ├── OrderRepositoryImpl.java
│   │   │       └── RefundRepositoryImpl.java
│   │   └── utils
│   │       └── CursorValue.java
│   ├── security
│   │   └── services
│   │       ├── UserDetailsImpl.java
│   │       └── UserDetailsServiceImpl.java
│   └── services
│       ├── mail
│       │   ├── MockMailService.java
│       │   ├── RestApiResendMailService.java
│       │   ├── RestApiSendgridMailService.java
│       │   ├── SmtpMailSender.java
│       │   └── templates
│       │       ├── MailTemplateFactory.java
│       │       ├── MailTemplate.java
│       │       ├── StockAlertTemplate.java
│       │       └── StockWarningTemplate.java
│       └── storage
│           ├── FakeGCPStorageService.java
│           └── GCPStorageService.java
├── interfaces
│   ├── dto
│   │   ├── auth
│   │   │   ├── AuthResponse.java
│   │   │   ├── ChangePasswordRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── PasswordResetResponse.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   ├── RefreshTokenResponse.java
│   │   │   ├── ResetPasswordRequest.java
│   │   │   └── SignupRequest.java
│   │   ├── commerce
│   │   │   ├── category
│   │   │   │   ├── CategoryResponse.java
│   │   │   │   ├── CategoryShortResponse.java
│   │   │   │   ├── CreateCategoryRequest.java
│   │   │   │   └── UpdateCategoryRequest.java
│   │   │   └── product
│   │   │       ├── CreateProductRequest.java
│   │   │       ├── ProductFullResponse.java
│   │   │       ├── ProductImageResponse.java
│   │   │       ├── ProductLikeResponse.java
│   │   │       ├── ProductListResponse.java
│   │   │       ├── ProductPriceHistoryResponse.java
│   │   │       ├── ProductStockResponse.java
│   │   │       └── UpdateProductRequest.java
│   │   ├── common
│   │   │   ├── MessageResponse.java
│   │   │   ├── Pageable.java
│   │   │   └── PaginatedResponse.java
│   │   └── sales
│   │       ├── AddItemRequest.java
│   │       ├── CartItemResponse.java
│   │       ├── CartResponse.java
│   │       ├── CheckoutRequest.java
│   │       ├── CheckoutResponse.java
│   │       ├── OrderHistoryResponse.java
│   │       ├── OrderItemResponse.java
│   │       ├── OrderResponse.java
│   │       ├── OrderWithHistory.java
│   │       └── UpdateOrderStatus.java
│   └── validation
│       ├── annotations
│       │   ├── ExistEmail.java
│       │   ├── MaxFileSize.java
│       │   ├── StrongPassword.java
│       │   ├── ValidEmail.java
│       │   ├── ValidImage.java
│       │   └── ValidImageList.java
│       └── validators
│           ├── ExistEmailValidator.java
│           ├── MaxFileSizeValidator.java
│           ├── StrongPasswordValidator.java
│           ├── ValidEmailValidator.java
│           ├── ValidImageListValidator.java
│           └── ValidImageValidator.java
└── TCommerceApplication.java
```

## 🚀 Getting Started

### Local Development
To run the project locally, follow these steps:
1. **Configure Properties**: Copy the local example properties file to the main `application.properties`:
   ```bash
   cp src/main/resources/example.local.properties src/main/resources/application.properties
   ```
2. **Environment Variables**: Open `src/main/resources/application.properties` and fill in the necessary values (database credentials, Stripe keys, etc.).
3. **Run Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Production Deployment
For production environments, the project is configured to run on **Google Cloud Run**:
1. **Production Properties**: Copy the production example properties:
   ```bash
   cp src/main/resources/example.prod.properties src/main/resources/application.properties
   ```
2. **Deploy with Cloud Build**: Use the following command to build the image and deploy to Cloud Run as defined in `cloudbuild.yaml`:
   ```bash
   gcloud builds submit --config cloudbuild.yaml .
   ```

---

## 📘 API Documentation

The API is fully documented using **OpenAPI 3.0**. You can explore the endpoints, request bodies, and responses interactively.

### Local Swagger UI
When the application is running, access the documentation at:
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Key Features Documented:
- **Authentication**: JWT login/signup and password recovery.
- **Product Management**: Category browsing, advanced filtering, and stock management.
- **Cart & Orders**: Full checkout flow and order status tracking.
- **Admin Tools**: Exclusive endpoints for managers to control inventory and orders.

---

## 🧩 Entity Relationship Diagram (ERD)

The following diagram represents the core database design:

![ERD Diagram](docs/ERD.png)
