# MR Candy App

## Project Overview

This project is a comprehensive application built using Spring Boot. It integrates various technologies and best practices to create a robust and secure application. The application features user authentication with JWT, email verification, integration with Firebase, and extensive use of Spring technologies including Spring Data JPA, Spring Security, and Spring Batch.

## Technologies Used

- **`Spring Boot (lastest version 3.3.2)`** : A powerful, feature-rich framework for building Java applications.
- **`Spring Data JPA`** : Simplifies database interactions by providing a robust data access layer.
- **`Spring Security`** : Utilized along with JSON Web Tokens (`JWT`) for secure access to the application's endpoints.
- **`JUnit & Mockito`** : Testing frameworks for unit and integration tests.
- **`Firebase`** : Integration for notifications and additional services.
- **`Logging`** : For application-level logging.
- **`HikariCP`** : High-performance JDBC connection pool.
- **`Spring Batch`** : Handles batch processing tasks efficiently.
- **`MySQL`** : A reliable relational database management system used for storing the application's data.
- **`Java 21`** : The latest long-term support (LTS) version of Java, offering new features and improvements.
- **`MapStruct`** : Simplifies mapping between layers.
- **`Spring DevTools`** : Enhances development experience by providing hot reloading and other useful features.
- **`Lombok`** : Reduces boilerplate code by providing annotations to automatically generate getters, setters, constructors, and other common methods.
- **`Hibernate ORM`** : An object-relational mapping (ORM) framework for Java applications, simplifying database interactions by mapping Java objects to database tables.
- **`SOLID Principles`** : For clean and maintainable code.
- **`Spring Validation`** : For validating user inputs.
- **`DTO (Data Transfer Objects)`** : For data transfer between layers.
- **`Projection`** : For retrieving specific data from repositories.
- **`SQL Injection Protection`** : Safeguarding against SQL injection attacks.
- **`Pagination`** : Efficiently handles large data sets by retrieving data in chunks.

## Features

- **`User Authentication`** : Secure login with JWT and refresh tokens.
- **`Email Verification`** : Sends verification emails and confirms user addresses.
- **`Firebase Integration`** : Utilize Firebase for additional features.
- **`Batch Processing`** : Handle large data sets and processing tasks.
- **`Bulk Operations`** : Efficiently manage bulk delete operations.
- **`Logical Operations`** : For complex business logic.
- **`Role-Based Access Control`** : Provides different levels of access based on user roles.
- **`Secure Data Handling`** : Ensures protection against SQL injection and other security threats.
- **`TOTP Algorithm`** : To Generate Secured Otp.
- **`Pagination`** : Handles data retrieval efficiently in chunks to improve performance.
- **`Database Indexs`** : Enhance database query performance by creating indexes on frequently searched columns.
- **`Stored Procedures`** : Predefined SQL procedures stored in the database to encapsulate complex logic and improve performance.

## Project Structure

```

MR-Candy-App
│
├── src
│ ├── main
│ │ ├── java
│ │ │ └── com
│ │ │ └── luv2code
│ │ │ └── demo
│ │ │ ├── config
│ │ │ │ ├── FirebaseConfiguration.java
│ │ │ │ ├── SecurityConfiguration.java
│ │ │ │ └── MrCandyAppApplication.java
│ │ │ ├── controller
│ │ │ │ ├── AuthenticationController.java
│ │ │ │ ├── CartController.java
│ │ │ │ ├── CategoryController.java
│ │ │ │ ├── CompanyController.java
│ │ │ │ ├── FileController.java
│ │ │ │ ├── NotificationController.java
│ │ │ │ ├── OrderController.java
│ │ │ │ ├── OtpController.java
│ │ │ │ ├── ProductController.java
│ │ │ │ └── UserController.java
│ │ │ ├── dto
│ │ │ │ ├── request
│ │ │ │ │ ├── CartItemRequestDTO.java
│ │ │ │ │ ├── CartRequestDTO.java
│ │ │ │ │ ├── ChangePasswordRequestDTO.java
│ │ │ │ │ ├── CompanyRequestDTO.java
│ │ │ │ │ ├── LoginRequestDTO.java
│ │ │ │ │ ├── RegisterRequestDTO.java
│ │ │ │ │ ├── UpdateUserRequestDTO.java
│ │ │ │ │ └── UpdateUserProfileRequestDTO.java
│ │ │ │ ├── response
│ │ │ │ │ ├── ApiResponseDTO.java
│ │ │ │ │ ├── CartItemResponseDTO.java
│ │ │ │ │ ├── CategoryResponseDTO.java
│ │ │ │ │ ├── CompanyResponseDTO.java
│ │ │ │ │ ├── DiscountedProductsResponseDTO.java
│ │ │ │ │ ├── JwtResponseDTO.java
│ │ │ │ │ ├── OrderItemResponseDTO.java
│ │ │ │ │ ├── ProductBestSellerResponseDTO.java
│ │ │ │ │ ├── ProductCompanyResponseDTO.java
│ │ │ │ │ ├── ProductDetailsCategoryResponseDTO.java
│ │ │ │ │ ├── ProductDetailsCompanyResponseDTO.java
│ │ │ │ │ ├── ProductDetailsResponseDTO.java
│ │ │ │ │ ├── UpdateUserProfileResponseDTO.java
│ │ │ │ │ ├── UserAuthenticationResponseDTO.java
│ │ │ │ │ └── UserTokenResponseDTO.java
│ │ │ │ ├── CategorySetterDTO.java
│ │ │ │ ├── CompanySetterDTO.java
│ │ │ │ ├── NotificationMessage.java
│ │ │ │ ├── ProductCartSetterDTO.java
│ │ │ │ ├── ProductGetterDTO.java
│ │ │ │ ├── ProductSetterDTO.java
│ │ │ │ ├── SystemMapper.java
│ │ │ │ └── UserSetterDTO.java
│ │ │ ├── entity
│ │ │ │ ├── Address.java
│ │ │ │ ├── Cart.java
│ │ │ │ ├── CartItem.java
│ │ │ │ ├── Category.java
│ │ │ │ ├── Company.java
│ │ │ │ ├── Order.java
│ │ │ │ ├── OrderItem.java
│ │ │ │ ├── Otp.java
│ │ │ │ ├── Product.java
│ │ │ │ ├── RefreshToken.java
│ │ │ │ ├── Role.java
│ │ │ │ └── User.java
│ │ │ ├── exc
│ │ │ │ ├── ErrorResponse.java
│ │ │ │ ├── GlobalExceptionHandler.java
│ │ │ │ ├── StatusCode.java
│ │ │ │ ├── CalculationException.java
│ │ │ │ ├── CustomAuthenticationEntryPoint.java
│ │ │ │ ├── ExpiredException.java
│ │ │ │ ├── NotFoundException.java
│ │ │ │ ├── NotFoundTypeException.java
│ │ │ │ └── QuantityNotAvailableException.java
│ │ │ ├── filter
│ │ │ │ └── JwtAuthenticationFilter.java
│ │ │ ├── helper
│ │ │ │ ├── FileHelper.java
│ │ │ │ ├── OtpGenerator.java
│ │ │ │ ├── PaginationHelper.java
│ │ │ │ ├── SecretKeyGenerator.java
│ │ │ │ └── impl
│ │ │ │ ├── FileHelper.java
│ │ │ │ ├── OtpGenerator.java
│ │ │ │ └── PaginationHelper.java
│ │ │ ├── repository
│ │ │ │ ├── CartRepository.java
│ │ │ │ ├── CategoryRepository.java
│ │ │ │ ├── CompanyRepository.java
│ │ │ │ ├── OrderItemRepository.java
│ │ │ │ ├── OrderRepository.java
│ │ │ │ ├── OtpRepository.java
│ │ │ │ ├── ProductRepository.java
│ │ │ │ ├── RefreshTokenRepository.java
│ │ │ │ ├── RoleRepository.java
│ │ │ │ └── UserRepository.java
│ │ │ ├── security
│ │ │ │ └── SecurityUser.java
│ │ │ ├── service
│ │ │ │ ├── IAuthenticationService.java
│ │ │ │ ├── ICartService.java
│ │ │ │ ├── ICategoryService.java
│ │ │ │ ├── ICompanyService.java
│ │ │ │ ├── IEmailService.java
│ │ │ │ ├── IOrderService.java
│ │ │ │ ├── IProductService.java
│ │ │ │ ├── IRefreshTokenService.java
│ │ │ │ ├── IUserService.java
│ │ │ │ └── impl
│ │ │ │ ├── AuthenticationService.java
│ │ │ │ ├── CartService.java
│ │ │ │ ├── CategoryService.java
│ │ │ │ ├── CompanyService.java
│ │ │ │ ├── EmailService.java
│ │ │ │ ├── FirebaseMessagingService.java
│ │ │ │ ├── JwtService.java
│ │ │ │ ├── LogoutService.java
│ │ │ │ ├── OrderService.java
│ │ │ │ ├── OtpService.java
│ │ │ │ ├── ProductService.java
│ │ │ │ ├── RefreshTokenService.java
│ │ │ │ └── UserService.java
│ │ │ ├── utils
│ │ │ │ └── FileUtils.java
│ │ └── resources
│ │ └── application.properties
└── pom.xml

```

## Installation

- **`Clone the repository`**:

  - git clone https://github.com/ahmedelazab1220/MR-Candy-App
  - you can also download Zip file and extract it.

- **`Set up the MySQL database`**:

  ```

    spring.datasource.url=jdbc:mysql://localhost:3306/[your_database_name]?useSSL=false
    spring.datasource.username=[your_username]
    spring.datasource.password=[your_password]
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    spring.jpa.properties.hibernate.format.sql=true

  ```

  **_`note`_** : if you need to create database automatically you can use this `spring.datasource.url=jdbc:mysql://localhost:3306/[your_database_name]?createDatabaseIfNotExist=true` instead of first line

  **_`note`_** : you can also see sql for database from this link <a href = "https://github.com/ahmedelazab1220/MR-Candy-App/blob/main/src/main/resources/database.sql"> Database.Sql </a> and modify on it.

- **`Build and run the application`**:

  - mvn clean install
  - mvn spring-boot:run

## Entity RelationShip Diagram(ERD)

![ERD](https://github.com/user-attachments/assets/8b340597-9f37-44da-86ae-1926512231c7)

## License

This project is licensed under the Apache License 2.0 - see the <a href = "https://github.com/ahmedelazab1220/MR-Candy-App/blob/main/LICENSE"> LICENSE </a> file for details.

## Conclusion

The MR Candy App is a well-structured and robust application that demonstrates the effective use of Spring Boot and related technologies to build a secure, scalable, and maintainable application. By leveraging best practices like SOLID principles, using DTOs for data transfer, implementing role-based access control, and securing data handling, this application is designed to meet the needs of modern web development. The extensive use of Spring Security for user authentication and Firebase for additional services ensures that the application is both powerful and versatile. This project is an excellent foundation for further expansion, whether you need to add new features, optimize performance, or scale the application.
