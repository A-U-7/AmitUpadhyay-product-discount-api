# Product Discount API

A Spring Boot-based RESTful API for managing product discounts in an e-commerce system. This service provides endpoints to apply various types of discounts to products based on different criteria.

## Features

- Apply percentage-based discounts to products
- Support for bulk discount calculations
- Configurable discount rules and strategies
- RESTful API endpoints for easy integration
- Built with Spring Boot for rapid development and deployment
- Maven-based build system

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/A-U-7/AmitUpadhyay-product-discount-api.git
cd AmitUpadhyay-product-discount-api
```

### Build the Application

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v2/api-docs`

## Available Endpoints

### Apply Discount

```
POST /api/discounts/apply
```

**Request Body:**
```json
{
  "productId": "12345",
  "originalPrice": 100.00,
  "discountType": "PERCENTAGE",
  "discountValue": 10.0
}
```

**Response:**
```json
{
  "productId": "12345",
  "originalPrice": 100.00,
  "discountAmount": 10.00,
  "finalPrice": 90.00,
  "discountType": "PERCENTAGE",
  "discountValue": 10.0
}
```

## Configuration

Application properties can be configured in `src/main/resources/application.properties`.

## Testing

Run the test suite with:

```bash
mvn test
```

## CI/CD

This project includes a `Jenkinsfile` for continuous integration and deployment. The pipeline includes:
- Build
- Test
- Code quality checks
- Deployment (configured in Jenkins)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


