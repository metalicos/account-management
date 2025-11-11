# Account Management - Full Stack Application

Повноцінний full-stack додаток для управління користувацькими акаунтами.

## Структура проекту

```
AccountManagement/
├── frontend/              # Angular додаток
│   ├── src/
│   │   └── app/
│   └── package.json
├── src/                   # Spring Boot додаток
│   └── main/
│       ├── java/
│       └── resources/
├── build.gradle
└── README.md
```

## Backend (Spring Boot)

### Запуск

```bash
# З профілем local (H2 in-memory)
./gradlew bootRun

# З профілем dev (PostgreSQL)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### API Endpoints

- `POST /api/auth/register` - Реєстрація
- `POST /api/auth/login` - Авторизація
- `POST /api/auth/refresh` - Оновлення токена
- `GET /api/users` - Список користувачів
- `GET /api/users/top` - Топ 10 користувачів
- `GET /api/users/:id` - Деталі користувача
- `PUT /api/users/:id` - Оновлення (ADMIN)
- `DELETE /api/users/:id` - Видалення (ADMIN)

## Frontend (Angular)

### Запуск

```bash
cd frontend
npm install
npm start
```

Додаток буде доступний за адресою: `http://localhost:4200`

### Маршрути

- `/login` - Авторизація
- `/register` - Реєстрація
- `/users` - Список користувачів (захищено)
- `/users/:id` - Деталі користувача (захищено)

## Повний запуск

### 1. Запустіть Backend

```bash
./gradlew bootRun
```

Backend буде доступний на `http://localhost:8080`

### 2. Запустіть Frontend

```bash
cd frontend
npm start
```

Frontend буде доступний на `http://localhost:4200`

### 3. Відкрийте браузер

Перейдіть на `http://localhost:4200` та використовуйте додаток.

## Тестування

### Backend тести

```bash
./gradlew test
```

### Frontend тести

```bash
cd frontend
npm test
```

## Документація

- [Postman Collection](AccountManagement.postman_collection.json) - для тестування API
- [Postman Environment](AccountManagement.postman_environment.json) - змінні середовища
- [Postman Setup Guide](POSTMAN_SETUP.md) - інструкції з налаштування
- [Frontend README](frontend/README.md) - документація Angular додатку

## Технології

### Backend
- Spring Boot 3.5.7
- Spring Security + JWT
- Hibernate/JPA
- H2 (local) / PostgreSQL (dev)
- Liquibase (YAML)
- Spring Cache
- Java 21

### Frontend
- Angular 20.3.0
- TypeScript
- RxJS
- Standalone Components
- Reactive Forms

