# Car Sharing Service 

## Overview
The Car Sharing Service is a web-based platform designed to manage car sharing
inventory, rentals, customers, notifications, and payments.
This project includes functionalities for user authentication,
car inventory
management, rental operations, and payment processing using
Stripe. Additionally, it features a notification service 
through Telegram to keep administrators informed about
important events such as new rentals, overdue rentals,
and successful payments.

## Technologies and tools 
This project utilizes a variety of technologies and tools to ensure
a robust, scalable, and maintainable car sharing service. 
Below is an overview of the main technologies and tools used:
### Backend:
   - **Java**: The primary programming language used for the backend services.
   - **Spring Boot**: A powerful framework for building production-ready applications quickly, 
providing features such as dependency injection, web frameworks, data access, and security.
   - **Spring Security**: Used to handle authentication and authorization.
   - **Spring Data JPA**: Simplifies data access and management using Java Persistence Api.
   - **Liquibase**: An open-source library for tracking, managing, and applying database schema changes.
   - **Stripe Api**: User for handling payment processing securely.
### Database:
   - **MySql**: A widely-used open-source relational database system, chosen for its reliability, 
ease of use, and support for complex queries and transactions.
### Testing:
   - **JUnit**: A widely used testing framework for Java applications.
   - **Mockito**: A mocking framework for unit test in Java.
   - **Spring Boot Test**: Provide utilities for testing Spring Boot applications.
### CI/CD:
   - **GitHub Actions**: Provides CI capabilities to automate building and testing the application.
### Containerization:
   - **Docker**: Used to create, deploy, and run application in containers.
   - **Docker Compose**: Used for defining and running multi-container Docker application.
### API Documentation: 
   - **Swagger/OpenApi**: Tools for generating and visualizing API documentation, making it easier to understand and use the API endpoints.
### Code Quantity:
   - **Checkstyle**: A development tool to help ensure that Java code adheres a coding standard.
   - **Maven**: A build automation tool used primary for Java projects. It simplifies the build process and dependency management.
### Notification Service:
   - **Telegram API**: Used for sending notification about rentals and payments to administrator via a Telegram bot.
### Projects Management:
   - **Trello**: A task management tool used for tracking project progress and managing tasks.
### Development Environment:
   - **IntelliJIdea**: An integrated development environment for Java development.
   - **Postman**: A tool for testing APIs by sending request and receiving response.
### Environment managing: 
   - **dotenv (.env)**: A module that loads environment variables from a '.env' file into the application, ensuring sensitive information is kept secure and not pushed to version control.

## Feature and functionality:
### User management:
1. **Registration**: Users can register on the platform by providing their email, first name, last name, and password.
    - [Registers a new user - demo.](https://drive.google.com/file/d/1V78I2bf0_0jdg136h_TWa6mlFnaiI3Cm/view?usp=sharing) 
    ```bash
   POST: /auth/register
   ``` 
   
2. **Authentication**: Secure login functionality using JWT tokens to manage sessions.Users can log in using their credentials (email and password).
    - [Sign in for existing user - demo.](https://drive.google.com/file/d/1Dwyg90Y1mwMzB_VOF0zFdXJDFy4wr7dF/view?usp=drive_link)
   ```bash
   POST: /auth/login
   ```
   
3. **User Profiles**: Users can view and updated their profile information.
   - [View profile - demo](https://drive.google.com/file/d/1o0v-JUnE_YOnaHM8NhMZgkzYLas2J4yc/view?usp=drive_link)
   ```bash
   GET: /user/me
   ```
   - [Update profile - demo](https://drive.google.com/file/d/1m8lhnajRDTp_ADoLdreP7jswQwkZcwC9/view?usp=drive_link)
   ```bash
   PUT: /users/me
   ```
4. **Role Management**: Admins can assign roles (ADMIN or USER) to users, controlling access to certain features.
### Car inventory management:
1. **Add Cars**: Admins can add new cars to the inventory, specifying details such as model, brand, type, inventory count, and daily fee.
   - [Add carts - demo](https://drive.google.com/file/d/1e1VVXpUHD9JFczJqteKxDjdv77k1QeQK/view?usp=drive_link)
   ```bash
   POST: /cars
   ```
2. **Update Cars**: Admins can update car details and manage the inventory count.
   - [Update cars - demo](https://drive.google.com/file/d/1nY2XxKVj2U9F9aqX3Wr35fvRn-k8n3lQ/view?usp=drive_link)
   ```bash
   PUT: /cars/{carId}
   ```
3. **Delete Cars**: Admins can remove cars from the inventory.
   - [Delete cars - demo](https://drive.google.com/file/d/1t9OVvn0bdygVDsh3f0_ydwkmd8TOuq-6/view?usp=drive_link)
   ```bash
   DELETE: /cars/{carId}
   ```
4. **View Cars**: All users, including unauthenticated users, can view the list of available cars and detailed information about each car.
   - [View cars - demo](https://drive.google.com/file/d/1bmW9W3h1kQtfhU1GhV8XDo7oSsCWxhBN/view?usp=sharing)
   ```bash
   GET: /cars
   ```
   ```bash
   GET: /cars/{carId}
   ```
### Rental Management:
1. **Create Rental**: Users can rent cars,  decreasing the inventory count by 1.
   - [Create rental - demo](https://drive.google.com/file/d/1iKxJ6BGJhFB-E2rdtG4pIwyyytV5Pmtp/view?usp=sharing)
   ```bash
   POST: /rentals
   ```
2. **View Rental**: Users can view their active and past rentals. Admins can view rentals for all users.
   - [View rental - demo](https://drive.google.com/file/d/1jn-tGLpGG6oG4AJ9tANbmB6lK8tfNfsD/view?usp=sharing)
   ```bash
   GET: /rentals
   ```
3. **Return Rental**: Users can return rented cars, which increase the inventory count by 1.
   - [Return rental - demo](https://drive.google.com/file/d/1UE20OYRPTPcTTl63THzrwPNc69OAA5j4/view?usp=sharing)
   ```bash
   POST: /return/{rentalId}
   ```
4. **Return Filtering**: Users can filter rentals by status (active or returned) and admins can filter rentals by user ID.
   ```bash
   GET: /rentals/?user_id=...&is_active=...
   ```
### Payment Processing
1. **Create Payment Sessions**: Users can create payment sessions for their rentals using the Stripe API. The system calculates the total price based on the rental duration.
   ```bash
   POST: /payments
   ```
2. **Payment Status**: Users can check the status of their payments (PENDING or PAID).
   ```bash
   GET: /payments/{user_id}
   ```
3. **Payment Types**: Supports different payment types, including regular payments for rentals and fines for overdue returns.
4. **Payment Confirmation**: Users are redirected to appropriate endpoints upon successful or canceled payments.
   ```bash
   GET: /payments/success
   ```
   ```bash
   GET: /payments/cancel
   ```
### Notification
1. **Rental Notifications**: Notifications about new rentals are sent to administrators via Telegram.
2. **Overdue Rentals**: Daily notifications are sent to administrators for overdue rentals.
3. **Payment Notifications**: Notifications for successful payments are sent to administrators.
### Additional Features
1. **Health Check**: A health check endpoint to monitor the status of the application.
2. **API Documentation**: Integrated Swagger/OpenAPI documentation for easy exploration and testing of API endpoints.
3. **Security**: Secure handling of sensitive data using environment variables and best practices in security.

## Installation
### Pre requirements:
   - **Java Development Kit (JDK 21)**: Ensure that JDK is installed and the JAVA_HOME environment variable is set.
   - **Maven**: Install Maven for building the project.
   - **MySQL**: Install MySQL and create a database for the project.
   - **Docker**: Ensure Docker and Docker Compose are installed for containerization
### Steps:
1. **Clone the Repository** 
   ```bash
    git clone https://github.com/jv-feb24-team3/car-sharing-service.git
    cd car-sharing-service
   ```
2. **Set up Environments Variables**
   - Create a .env file in the root directory of the project.
   - Add the necessary environment variables. Use the provided .env.sample as a template.
   
   `Example .env file:`
   ```.env
    #MySQL Database Configuration:
     MYSQLDB_DATABASE=test
     MYSQLDB_USERNAME=your_user_name
     MYSQLDB_PASSWORD=_your_password
     MYSQLDB_ROOT_PASSWORD=your_password
     MYSQLDB_LOCAL_PORT=3308
     MYSQLDB_PORT=3306

   # Spring Boot Configuration
     SPRING_LOCAL_PORT=8082
     SPRING_DOCKER_PORT=8080

   # Debug Port
     DEBUG_PORT=5006

   # Telegram Bot Configuration
     BOT_USERNAME=your_telegram_user_name
     BOT_TOKEN=your_telegram_bot_token
     TELEGRAM_ADMIN_CHAT_ID=your_chatId

   # Stripe API Configuration
     STRIPE_API_KEY=your_key
     STRIPE_WEBHOOK_SECRET=your_webhook_secret

   # JWT Configuration
     JWT_SECRET=your_secret
     JWT_EXPIRATION=your_expiration
   ```
3. **Build the Project**
   ```bash
   mvn clean install
   ```
4. **Run the Application**
   - **Using Docker Compose**
   ```bash
   docker-compose up --build
   ```
   - **Using Maven**
   ```bash
   mvn spring-boot:run
   ```
5. **Access the Application**
   - The application should now be running on http://localhost:8082.
   - You can access the Swagger API documentation at http://localhost:8082/swagger-ui.html.
### Optional: Running Tests 
**To run the tests, use the following Maven command**:
   ```bash
   mvn test
   ```
## Challenges and Solutions
During the development of the Car Sharing Service, 
several challenges were encountered. Here are some of the key 
challenges and the solutions implemented to overcome them:
1. ### Secure Authentication and Authorization
    **Challenge**: Implementing a secure authentication system 
that handles user registration, login, and role-based 
access control while ensuring sensitive user data is protected. 

    **Solution**:
    - **JWT Tokens**: We used JWT (JSON Web Tokens) for secure authentication and session management. JWT allows for stateless authentication, making it scalable and efficient.
    - **Spring Security**: Integrated Spring Security to handle authentication and authorization, ensuring that only authorized users can access certain endpoints.
    - **Password Encryption**: Utilized strong hashing algorithms (e.g., BCrypt) to store passwords securely, ensuring user credentials are protected.
2. ### Database Schema Management
   **Challenge**: Managing and applying database schema changes consistently across different environments.
   
   **Solution**:
   - **Liquibase**: Integrated Liquibase for database migrations, allowing us to track, version, and deploy database schema changes systematically. This ensures that all environments (development, testing, production) are in sync with the latest database structure.
3. ### Payment Integration
   **Challenge**: Integrating with Stripe API to handle payments securely and efficiently.

   **Solution**:
   - **Stripe API**: Used Stripe API for creating payment sessions, handling transactions, and managing payment statuses.
   - **Payment Status Handling**: Implemented endpoints to handle payment success and cancellation, ensuring users are informed of the payment status and can take appropriate actions.
4. ### Real-time Notifications
   **Challenge**: Implementing a real-time notification system to inform administrators about new rentals, overdue rentals, and successful payments.

   **Solution**:
   - **Telegram API**: Integrated with Telegram API to send real-time notifications to administrators. A Telegram bot was set up to handle the notifications.
5. ### Containerization and Deployment
   **Challenge**: Ensuring the application is easy to deploy and run in different environments.
   
   **Solution**: 
   - **Docker**: Containerized the application using Docker, allowing it to run consistently across different environments.
   - **Docker Compose**: Used Docker Compose to manage multi-container setups, making it easy to set up the application along with its dependencies (e.g., MySQL) with a single command.
6. ### API Documentation
   **Challenge**: Providing clear and comprehensive API documentation for developers and users to interact with the service.
   
   **Solution**: Swagger/OpenAPI: Integrated Swagger for API documentation, providing an interactive interface for exploring and testing API endpoints. This improves developer experience and ensures clarity in how the API can be used.

## Authors:
   - [Oleksandr Farion](https://github.com/ReamFOX)
   - [Oksana Miazina](https://github.com/oksana-miazina)
   - [Denys Diuimov](https://github.com/Eidenn005)
   - [Mykola Skrypalov](https://github.com/kaiiiseeel)
   - [Dmytro Hadiuchko](https://github.com/DmytroHadiuchko)
