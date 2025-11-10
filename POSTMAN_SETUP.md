# Postman Collection Setup Guide

## Імпорт колекції та середовища

1. **Відкрийте Postman**
2. **Імпортуйте файли:**
   - Натисніть `Import` в лівому верхньому куті
   - Перетягніть або виберіть файли:
     - `AccountManagement.postman_collection.json`
     - `AccountManagement.postman_environment.json`
   - Натисніть `Import`

3. **Виберіть середовище:**
   - У правому верхньому куті виберіть `Account Management - Local` зі списку середовищ

## Автоматизація токенів

### Як це працює:

1. **Автоматичне збереження токенів:**
   - Після успішного запиту `Login` або `Register User`, токени автоматично зберігаються в змінних середовища
   - `accessToken` та `refreshToken` зберігаються після кожного успішного запиту

2. **Автоматичне використання токенів:**
   - Всі захищені запити автоматично використовують `accessToken` з середовища
   - Токен додається до заголовка `Authorization: Bearer {accessToken}`

3. **Оновлення токенів:**
   - Запит `Refresh Token` автоматично оновлює обидва токени в середовищі

## Послідовність тестування

### 1. Реєстрація користувача
```
POST /api/auth/register
```
- Створює нового користувача
- Зберігає `userId` та `userEmail` в середовищі

### 2. Авторизація
```
POST /api/auth/login
```
- Авторизує користувача
- **Автоматично зберігає `accessToken` та `refreshToken`**

### 3. Використання захищених endpoints
Всі наступні запити автоматично використовують збережений токен:
- `GET /api/users/top` - Топ 10 користувачів
- `GET /api/users` - Всі користувачі з пагінацією
- `GET /api/users/{id}` - Користувач за ID
- `PUT /api/users/{id}` - Оновлення (тільки ADMIN)
- `DELETE /api/users/{id}` - Видалення (тільки ADMIN)

### 4. Оновлення токена (якщо потрібно)
```
POST /api/auth/refresh
```
- Оновлює access token
- **Автоматично зберігає нові токени**

## Змінні середовища

| Змінна | Опис | Автоматично оновлюється |
|--------|------|------------------------|
| `baseUrl` | Базовий URL API (за замовчуванням: http://localhost:8080) | Ні |
| `accessToken` | JWT access token | Так (після login/refresh) |
| `refreshToken` | JWT refresh token | Так (після login/refresh) |
| `tokenType` | Тип токена (завжди "Bearer") | Ні |
| `userId` | ID зареєстрованого користувача | Так (після register) |
| `userEmail` | Email користувача | Так (після register) |
| `i18nId` | ID інтернаціоналізації | Так (після додавання i18n) |

## Приклади використання

### Створення користувача з роллю ADMIN
```json
{
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@example.com",
    "phoneNumber": "+380501234567",
    "password": "AdminPass123!",
    "address": {
        "country": "Україна",
        "city": "Київ",
        "street": "Хрещатик",
        "building": "1"
    },
    "roles": ["ADMIN"]
}
```

### Створення користувача з роллю USER_PLATINUM
```json
{
    "firstName": "Platinum",
    "lastName": "User",
    "email": "platinum@example.com",
    "phoneNumber": "+380501234567",
    "password": "PlatinumPass123!",
    "address": {
        "country": "Україна",
        "city": "Київ",
        "street": "Хрещатик",
        "building": "1"
    },
    "roles": ["USER_PLATINUM"]
}
```

## Налаштування для різних середовищ

### Local (H2)
```json
{
    "baseUrl": "http://localhost:8080"
}
```

### Dev (PostgreSQL)
```json
{
    "baseUrl": "http://localhost:8080"
}
```

### Production
```json
{
    "baseUrl": "https://your-production-domain.com"
}
```

## Troubleshooting

### Токен не додається автоматично
- Перевірте, чи вибрано правильне середовище (`Account Management - Local`)
- Переконайтеся, що запит `Login` виконано успішно (код 200)
- Перевірте вкладку `Tests` в запиті `Login` - там має бути код для збереження токенів

### Помилка 401 Unauthorized
- Переконайтеся, що токен збережено після логіну
- Спробуйте оновити токен через `Refresh Token`
- Перевірте, чи не закінчився термін дії токена (1 година для access token)

### Помилка 403 Forbidden
- Перевірте ролі користувача
- Деякі endpoints доступні тільки для `ADMIN` ролі

## Додаткові можливості

### Колекція містить:
- ✅ Автоматичне збереження токенів
- ✅ Автоматичне використання токенів у захищених запитах
- ✅ Приклади всіх endpoints
- ✅ Тести для автоматичного збереження змінних
- ✅ Документацію для кожного запиту

### Рекомендації:
- Використовуйте окремі середовища для різних профілів (local, dev, prod)
- Зберігайте різні email для тестування різних ролей
- Використовуйте `Refresh Token` перед закінченням терміну дії access token

