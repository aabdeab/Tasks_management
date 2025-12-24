# Task Manager Application

A complete task and project management application with JWT authentication.

## 🛠️ Tools Used

### Backend
- **Java 17**
- **Spring Boot 3.2.5**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring OAuth2 Resource Server
- **PostgreSQL 16** - Relational Database
- **JWT (JSON Web Tokens)** - Authentication
- **Lombok** - Boilerplate code reduction
- **Jackson** - JSON Serialization/Deserialization
- **Hibernate Validator** - Data validation
- **Gradle** - Dependency management
- **Docker & Docker Compose** - Containerization

### Frontend
- *Coming soon*

## 📋 Prerequisites

- **Java 17** or higher
- **Docker** and **Docker Compose**
- **Git**
- **Gradle** (optional, wrapper is included)

## 🚀 Installation and Setup

### 1. Clone the project

```bash
git clone <repo-url>
cd TaskManager/Backend
```

### 2. Database Setup

#### Option A: Using Docker (Recommended)

1. Create a `.env` file at the project root with the following variables (default values are available in `application-dev.yml`):

```env
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=

JWT_SECRET=
JWT_EXPIRATION=
```

2. Start PostgreSQL with Docker Compose:

```bash
docker-compose up -d
```

3. Verify that the container is running:

```bash
docker ps
```

#### Option B: Local PostgreSQL

If you prefer using a local PostgreSQL installation:

1. Install PostgreSQL 16
2. Create a database `tasks_db`
3. Create a user with the credentials from the `.env` file
4. Update the `.env` file with your connection information

### 3. How to Run Backend

#### On Windows:

```cmd
gradlew.bat bootRun
```

#### On Linux/Mac:

```bash
./gradlew bootRun
```

The application will start on **http://localhost:8080**

### 4. Environment Profiles

The application supports multiple profiles:

- **dev** (default): Development with detailed logs
- **test**: Testing
- **prod**: Production

To change profile:

```bash
gradlew.bat bootRun --args='--spring.profiles.active=prod'
```

## 📚 API Endpoints

### Authentication

| Method | Endpoint | Description | Authentication |
|---------|----------|-------------|------------------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | User login | No |
| GET | `/api/auth/whoami` | Get logged-in user information | No |

### Projects

| Method | Endpoint | Description | Authentication |
|---------|----------|-------------|------------------|
| GET | `/api/projects` | List user's projects | Yes |
| GET | `/api/projects/{id}` | Get project details | Yes |
| POST | `/api/projects` | Create a new project | Yes |
| PUT | `/api/projects/{id}` | Update a project | Yes |
| DELETE | `/api/projects/{id}` | Delete a project | Yes |
| GET | `/api/projects/{id}/progress` | Get project progress | Yes |

### Tasks

| Method | Endpoint | Description | Authentication |
|---------|----------|-------------|------------------|
| GET | `/api/tasks/project/{projectId}` | Get tasks for a project | Yes |
| GET | `/api/tasks/{id}` | Get task details | Yes |
| POST | `/api/tasks` | Create a new task | Yes |
| PUT | `/api/tasks/{id}` | Update a task | Yes |
| DELETE | `/api/tasks/{id}` | Delete a task | Yes |
| PATCH | `/api/tasks/{id}/complete` | Mark task as completed | Yes |

## 🧪 Testing with Postman

### 1. Register a User

**POST** `http://localhost:8080/api/auth/register`

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Password123!"
}
```

### 2. Login

**POST** `http://localhost:8080/api/auth/login`

```json
{
  "username": "testuser",
  "password": "Password123!"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "test@example.com"
}
```

**⚠️ Important**: Copy the token for subsequent requests!

### 3. Using the Token

For protected endpoints, add the following header:

```
Authorization: Bearer <your-token>
```

### 4. Identify User (Development)

**GET** `http://localhost:8080/api/auth/whoami`

Headers: `Authorization: Bearer <your-token>`

### 5. Create a Project

**POST** `http://localhost:8080/api/projects`

Headers: `Authorization: Bearer <your-token>`

```json
{
  "title": "My First Project",
  "description": "Description of my project"
}
```

### 6. Create a Task

**POST** `http://localhost:8080/api/tasks`

Headers: `Authorization: Bearer <your-token>`

```json
{
  "title": "My first task",
  "description": "Task description",
  "dueDate": "2025-12-31T23:59:59",
  "projectId": 1
}
```

## 🗄️ Database Structure

### Table `users`
- `id` (BIGSERIAL PRIMARY KEY)
- `username` (VARCHAR UNIQUE)
- `email` (VARCHAR UNIQUE)
- `password` (VARCHAR - bcrypt hash)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Table `projects`
- `id` (BIGSERIAL PRIMARY KEY)
- `title` (VARCHAR)
- `description` (TEXT)
- `user_id` (BIGINT FOREIGN KEY → users.id)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Table `tasks`
- `id` (BIGSERIAL PRIMARY KEY)
- `title` (VARCHAR)
- `description` (TEXT)
- `due_date` (TIMESTAMP)
- `completed` (BOOLEAN)
- `project_id` (BIGINT FOREIGN KEY → projects.id)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## 🔧 Configuration

### application.yml

The `application.yml` file contains the main configuration:
- Server port: 8080
- JPA/Hibernate configuration
- Security settings

### Profiles

- **application-dev.yml**: Development configuration
- **application-test.yml**: Test configuration

## 📝 Logs

Logs are configured with SLF4J and Logback. Services log:
- CRUD operations
- Errors and exceptions
- Debug information in dev mode

## 🐛 Error Handling

The application uses a `GlobalExceptionHandler` to handle:
- Data validation (`400 Bad Request`)
- Resources not found (`404 Not Found`)
- Forbidden access (`403 Forbidden`)
- Internal errors (`500 Internal Server Error`)

## 🔒 Security

- **JWT Authentication**: Tokens expire after ~58 minutes
- **Bcrypt**: Password hashing
- **CORS**: Configured to accept all origins (modify in production)
- **Public endpoints**: `/api/auth/**`
- **Protected endpoints**: All other endpoints

## 🛑 Stopping the Application

### Stop the Backend
Press `Ctrl+C` in the terminal

### Stop PostgreSQL (Docker)
```bash
docker-compose down
```

To also remove data:
```bash
docker-compose down -v
```

## 📦 Production Build

To create an executable JAR:

```bash
gradlew.bat build
```

The JAR will be generated in `build/libs/`

To run it:

```bash
java -jar build/libs/TaskManager-1.0-SNAPSHOT.jar
```

## 🤝 Contributing

1. Fork the project
2. Create a branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



## 👨‍💻 Author

AABDANE ABDELKARIM

---

**Note**:  this is only the backend part of the application. The frontend is here  https://github.com/aabdeab/Task_Manager

demo :  https://drive.google.com/file/d/1kmfvSfKUULGv7cpSEnN18fHmCT1mnC_M/view?usp=sharing

