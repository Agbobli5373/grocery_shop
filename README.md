# Grocery Shop 🛒

A modern, full-stack e-commerce grocery shopping application inspired by Picnic, featuring real-time order tracking, event-driven architecture, and server-driven UI.

## 📋 Overview

This project is a comprehensive grocery shopping platform built with cutting-edge technologies. It consists of a robust Spring Boot backend implementing Domain-Driven Design (DDD) with modular architecture, and a responsive Next.js frontend with real-time Server-Sent Events (SSE) integration.

### Key Features

- **Event-Driven Architecture**: Real-time order processing with RabbitMQ
- **Modular Backend**: Spring Modulith for clean domain separation
- **Real-Time Updates**: Server-Sent Events for live order tracking
- **Responsive Frontend**: Mobile-first design with Tailwind CSS
- **Modern Tech Stack**: Java 24, Next.js 15, TypeScript, PostgreSQL
- **Comprehensive Testing**: Unit, integration, and E2E test suites
- **Production Ready**: Docker deployment with proper CI/CD

## 🏗️ Architecture

### Backend (Spring Boot)

```
Event-Driven Microservices with DDD
├── Auth Module (JWT Authentication)
├── Product Module (Catalog Management)
├── Cart Module (Shopping Cart)
├── Order Module (Order Processing)
├── Notification Module (Email/Push)
├── Admin Module (Dashboard & Analytics)
└── Shared Infrastructure (SSE, Events, DTOs)
```

**Key Components:**
- **Spring Modulith**: Domain-driven modular architecture
- **RabbitMQ**: Asynchronous event messaging
- **PostgreSQL**: Production database with H2 for development
- **JWT Security**: Role-based authentication
- **Flyway Migrations**: Database version control
- **OpenAPI/Swagger**: Automated API documentation

### Frontend (Next.js)

```
Server-Driven UI with Real-Time Features
├── App Router (File-based routing)
├── Components (Reusable UI library)
├── Store (Zustand + React Query)
├── API (Axios with interceptors)
├── SSE (Real-time order tracking)
└── Forms (React Hook Form + Zod)
```

**Key Components:**
- **Next.js 15**: App Router with Turbopack
- **TypeScript**: Type-safe development
- **Tailwind CSS**: Utility-first styling
- **Server-Sent Events**: Real-time order status updates
- **React Query**: Server state management

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.0
- **Language**: Java 24 (latest LTS)
- **Database**: PostgreSQL (prod) / H2 (dev)
- **Message Broker**: RabbitMQ
- **Security**: Spring Security with JWT
- **Modularity**: Spring Modulith
- **Migrations**: Flyway
- **Testing**: JUnit 5, Testcontainers
- **Documentation**: OpenAPI 3.0

### Frontend
- **Framework**: Next.js 15.5.4 (App Router)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS 4.x
- **State**: Zustand + React Query
- **API**: Axios with interceptors
- **Forms**: React Hook Form + Zod
- **Testing**: Jest + Playwright

### DevOps & Tools
- **Build**: Maven (backend), npm (frontend)
- **Container**: Docker + Docker Compose
- **IDE**: IntelliJ IDEA (recommended)
- **Version Control**: Git
- **CI/CD**: GitHub Actions (planned)

## 📁 Project Structure

```
grocery-shop/
├── grocery-shop-backend/          # Spring Boot application
│   ├── src/main/java/com/groceryshop/
│   │   ├── auth/                  # Authentication domain
│   │   ├── product/               # Product management
│   │   ├── cart/                  # Shopping cart
│   │   ├── order/                 # Order processing
│   │   ├── notification/          # Email/push notifications
│   │   ├── admin/                 # Admin dashboard
│   │   ├── shared/                # Cross-cutting concerns
│   │   ├── sse/                   # Server-sent events
│   │   └── GroceryShopApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml        # Main configuration
│   │   └── db/migration/          # Flyway scripts
│   └── pom.xml                    # Maven dependencies
├── grocery-shop-frontend/         # Next.js application
│   ├── src/
│   │   ├── app/                   # App router pages
│   │   ├── components/            # Reusable components
│   │   ├── lib/                   # Utilities & API client
│   │   └── types/                 # TypeScript definitions
│   ├── public/                    # Static assets
│   ├── package.json               # Dependencies
│   ├── tailwind.config.ts         # Styling config
│   └── next.config.ts             # Next.js config
├── docker-compose.yml             # Multi-container setup
├── README.md                      # This file
└── .gitignore
```

## 🚀 Quick Start

### Prerequisites

- **Java 24** (JDK)
- **Node.js 20+**
- **Maven 3.9+**
- **Docker & Docker Compose** (for external services)
- **Git**

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Agbobli5373/grocery_shop.git
   cd grocery_shop
   ```

2. **Start external services**
   ```bash
   docker-compose up -d rabbitmq postgres
   ```

3. **Backend Setup**
   ```bash
   cd grocery-shop-backend
   ./mvnw spring-boot:run
   ```
   Backend will be available at: http://localhost:8080

4. **Frontend Setup**
   ```bash
   cd grocery-shop-frontend
   npm install
   npm run dev
   ```
   Frontend will be available at: http://localhost:3000

### Docker Development

For a complete containerized setup:

```bash
docker-compose up --build
```

## ⚙️ Configuration

### Backend Environment Variables

Create `grocery-shop-backend/src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/grocery_shop
    username: grocery_user
    password: grocery_pass
  rabbitmq:
    host: localhost
    username: guest
    password: guest

app:
  jwt:
    secret: your-jwt-secret-key
    expiration: 900000
```

### Frontend Environment Variables

Create `grocery-shop-frontend/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_WS_URL=ws://localhost:8080
```

## 🧪 Testing

### Backend Tests

```bash
cd grocery-shop-backend
./mvnw test                    # Unit tests
./mvnw verify                  # Integration tests
./mvnw test -Dtest=*IT         # Run integration tests only
```

### Frontend Tests

```bash
cd grocery-shop-frontend
npm test                       # Unit tests
npm run test:e2e              # E2E tests
```

## 📡 API Documentation

### Backend APIs

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Key API Endpoints

#### Authentication
```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/refresh
GET    /api/auth/me
```

#### Products
```
GET    /api/products
GET    /api/products/{id}
POST   /api/products          # Admin only
PUT    /api/products/{id}     # Admin only
GET    /api/products/recommendations
```

#### Cart & Orders
```
GET    /api/cart
POST   /api/cart/items
POST   /api/cart/checkout
GET    /api/orders
GET    /api/orders/{id}/events  # SSE stream
```

## 🚢 Deployment

### Production Build

1. **Backend**
   ```bash
   cd grocery-shop-backend
   ./mvnw clean package -DskipTests
   ```

2. **Frontend**
   ```bash
   cd grocery-shop-frontend
   npm run build
   npm run start
   ```

### Docker Deployment

```bash
# Build and run complete stack
docker-compose -f docker-compose.prod.yml up --build
```

### Environment Setup

For production, set these environment variables:

```bash
# Backend
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://prod-db:5432/grocery_prod
JWT_SECRET=your-production-secret

# Frontend
NEXT_PUBLIC_API_URL=https://api.yourdomain.com
```

## 🤝 Contributing

1. **Fork the repository**

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow existing code style and patterns
   - Add tests for new functionality
   - Update documentation if needed

4. **Run tests**
   ```bash
   # Backend tests
   cd grocery-shop-backend && ./mvnw test

   # Frontend tests
   cd grocery-shop-frontend && npm test
   ```

5. **Commit your changes**
   ```bash
   git commit -m "Add: Brief description of changes"
   ```

6. **Push and create PR**
   ```bash
   git push origin feature/your-feature-name
   ```

### Development Guidelines

- **Backend**: Follow DDD principles and Spring Boot best practices
- **Frontend**: Use TypeScript strictly, follow React conventions
- **Testing**: Aim for 80%+ code coverage
- **Commits**: Use conventional commit format
- **PRs**: Include description and link related issues

## 📊 Monitoring & Observability

### Health Checks
- **Backend**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

### Logging
- Structured logging with Spring Boot
- Separate log files for different environments
- Correlation IDs for request tracing

### Performance
- Database query optimization
- Caching strategies (planned)
- CDN for static assets

## 🔍 Troubleshooting

### Common Issues

**Backend won't start:**
- Ensure Java 24 is installed: `java -version`
- Check database connectivity
- Verify RabbitMQ is running

**Frontend build fails:**
- Clear node_modules: `rm -rf node_modules && npm install`
- Check Node.js version: `node --version`

**SSE not working:**
- Check browser console for connection errors
- Verify backend CORS configuration
- Ensure RabbitMQ events are being published

### Debug Mode

Run with debug flags:
```bash
# Backend
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Frontend
DEBUG=* npm run dev
```

## 📚 Additional Documentation

- [Backend Architecture Plan](./plan.md)
- [Frontend Architecture Plan](./frontend-plan.md)
- [Testing Plan](./test-plan.md)
- [Frontend README](./grocery-shop-frontend/README.md)
- [API Documentation](./grocery-shop-backend/README.md) (planned)

## 🏆 Key Achievements

- **Event-Driven Design**: Real-time order processing with RabbitMQ
- **Domain-Driven Architecture**: Clean separation of business concerns
- **Real-Time UI**: Server-driven updates with SSE
- **Modern Stack**: Latest versions of all major frameworks
- **Production Ready**: Comprehensive testing and deployment pipeline
- **Modular Structure**: Easy to maintain and extend

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Isaac Agbobli** - *Full-stack Development*

## 🙏 Acknowledgments

- Inspired by Picnic's innovative grocery delivery model
- Built with modern Java and React ecosystems
- Thanks to Spring Boot and Next.js communities

---

**Happy Shopping! 🛒✨**
