# Specialist Hub - CPT202 Group 25 Project

A specialist appointment booking system built with Java Spring Boot and MySQL.

---

## Table of Contents

- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Test Accounts](#test-accounts)
- [API Endpoints](#api-endpoints)
- [Development](#development)
- [Git Workflow](#git-workflow)

---

## Technologies

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.2.5 | Backend framework |
| Spring Data JPA | - | ORM and data access |
| MySQL Connector | - | MySQL database driver |

### Frontend

| Technology | Purpose |
|------------|---------|
| HTML5 | Page structure |
| JavaScript (ES6+) | Client-side logic |
| Tailwind CSS | Styling |

### Database

| Technology | Version |
|------------|---------|
| MySQL | 8.0+ |

### Development Tools

| Tool | Purpose |
|------|---------|
| Maven | Dependency management and build |
| VS Code | Code editor |
| MySQL Workbench | Database management |

---

## Prerequisites

Ensure the following are installed before setup:

- **JDK 17** - [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/installer/)
- **Maven** (included via wrapper in project)
- **Git** - [Download](https://git-scm.com/download/win)

---

## Project Structure

```
CPT202/
├── specialist_order_system/          # Main project directory
│   ├── src/main/java/com/example/backend/
│   │   ├── controller/                # REST API controllers
│   │   │   ├── AdminController.java
│   │   │   ├── AppointmentController.java
│   │   │   ├── AvailabilityController.java
│   │   │   ├── CustomerController.java
│   │   │   ├── PaymentController.java
│   │   │   └── SpecialistController.java
│   │   ├── model/                     # Entity classes
│   │   │   ├── Admin.java
│   │   │   ├── Appointment.java
│   │   │   ├── Customer.java
│   │   │   ├── Order.java
│   │   │   ├── Payment.java
│   │   │   ├── Specialist.java
│   │   │   └── SpecialistAvailability.java
│   │   ├── repository/                # Data access layer
│   │   ├── scheduler/                # Scheduled tasks
│   │   └── BackendApplication.java    # Main entry point
│   ├── src/main/resources/
│   │   ├── static/                    # Frontend static files
│   │   │   ├── admin/                # Admin pages
│   │   │   ├── customer/             # Customer pages
│   │   │   └── specialist/            # Specialist pages
│   │   ├── application.properties     # Configuration
│   │   └── schema-admin.sql           # Database schema
│   ├── src/test/                     # Unit tests
│   ├── pom.xml                       # Maven configuration
│   └── specialist_hub_backup.sql     # Full database backup
└── README.md
```

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yi3328/CPT202-Group25.git
cd CPT202
```

### 2. Database Setup

#### Option A: Using MySQL Workbench

1. Open **MySQL Workbench**
2. Connect to your local MySQL instance
3. Go to **Server → Data Import**
4. Select **Import from Self-Contained File**
5. Browse to `specialist_order_system/specialist_hub_backup.sql`
6. Select target schema name: `specialist_hub`
7. Click **Start Import**

#### Option B: Using Command Line

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS specialist_hub;"
mysql -u root -p specialist_hub < specialist_order_system/specialist_hub_backup.sql
```

### 3. Configure Database Connection

Edit `specialist_order_system/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/specialist_hub
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Run the Backend

```bash
cd specialist_order_system

# Windows
.\mvnw clean spring-boot:run

# macOS / Linux
./mvnw clean spring-boot:run
```

The backend will start at **http://localhost:8080**.

### 5. Access the Frontend

Open the following file in your browser:
- Login page: `specialist_order_system/src/main/resources/static/index.html`

Or access via the running server:
- http://localhost:8080

---

## Test Accounts

| Role | Username | Password |
|------|-----------|----------|
| Customer | `CUST001` - `CUST020` | `123456` |
| Specialist | `SPEC001` - `SPEC005` | `123456` |
| Admin | `admin001` | `123456` |

---

## API Endpoints

### Customer APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/customer/login` | Customer login |
| GET | `/api/customer/{id}` | Get customer details |
| GET | `/api/customer/appointments/{customerId}` | Get customer appointments |

### Specialist APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/specialist/login` | Specialist login |
| GET | `/api/specialist/{id}` | Get specialist details |
| GET | `/api/specialist/availability/{id}` | Get specialist availability |

### Appointment APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointment/create` | Create appointment |
| GET | `/api/appointment/{id}` | Get appointment details |
| PUT | `/api/appointment/{id}/status` | Update appointment status |

### Payment APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payment/process` | Process payment |
| GET | `/api/payment/{orderId}` | Get payment status |

### Admin APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/login` | Admin login |
| GET | `/api/admin/specialists` | List all specialists |
| GET | `/api/admin/customers` | List all customers |

---

## Development

### Building the Project

```bash
cd specialist_order_system
.\mvnw clean package
```

### Running Tests

```bash
.\mvnw test
```

### Database Schema Updates

To apply schema updates to an existing database:

1. Open `schema-admin.sql` in MySQL Workbench
2. Execute the SQL statements against your database

---

## Git Workflow

### Before Starting Work

Always pull the latest changes before making modifications:

```bash
git pull origin main
```

### Committing Changes

```bash
git add .
git commit -m "Description of changes"
git push origin main
```

### Collaboration Guidelines

1. Create a new branch for new features: `git checkout -b feature-name`
2. Commit frequently with clear messages
3. Pull main branch before merging: `git pull origin main`
4. Resolve merge conflicts locally before pushing

---

## Troubleshooting

### Port 8080 Already in Use

Edit `src/main/resources/application.properties` and change the port:

```properties
server.port=8081
```

### Database Connection Failed

1. Verify MySQL service is running
2. Check username and password in `application.properties`
3. Ensure the database `specialist_hub` exists

### Maven Build Fails

```bash
.\mvnw clean install -U
```

---

## License

This project was developed by **CPT202 Group 25**.
