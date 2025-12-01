# language: ru
@wiremock
Функция: Пример использования WireMock для мокирования API

  Этот файл демонстрирует различные способы использования WireMock
  в E2E автотестах на Cucumber.

  Сценарий: Простой GET запрос с успешным ответом
    Дано API endpoint "/api/users" returns status 200
    # Здесь ваши шаги для UI теста
    # Когда пользователь открывает страницу пользователей
    # Тогда должен увидеть список пользователей

  Сценарий: GET запрос с JSON ответом
    Дано API endpoint "/api/users/1" returns status 200 with JSON:
      """
      {
        "id": 1,
        "name": "Иван Иванов",
        "email": "ivan@example.com",
        "role": "admin"
      }
      """
    # Когда пользователь открывает профиль пользователя #1
    # Тогда должен увидеть имя "Иван Иванов"
    # И должен увидеть email "ivan@example.com"

  Сценарий: POST запрос с успешной авторизацией
    Дано POST endpoint "/api/auth/login" returns status 200 with JSON:
      """
      {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        "userId": 1,
        "expiresIn": 3600
      }
      """
    # Когда пользователь вводит логин "test" и пароль "password"
    # И нажимает кнопку "Войти"
    # Тогда должен увидеть сообщение "Добро пожаловать"

  Сценарий: GET запрос с задержкой для тестирования таймаутов
    Дано API endpoint "/api/slow-service" returns status 408 with 5000ms delay
    # Когда пользователь пытается загрузить медленный сервис
    # Тогда должен увидеть сообщение об ошибке таймаута

  Сценарий: Различные HTTP методы
    Дано POST endpoint "/api/users" returns status 201 with JSON: "{\"id\": 2}"
    И PUT endpoint "/api/users/2" returns status 200
    И DELETE endpoint "/api/users/2" returns status 204
    # Когда пользователь создает нового пользователя
    # И редактирует его данные
    # И удаляет пользователя
    # Тогда операции должны выполниться успешно

  Сценарий: Мокирование ошибок API
    Дано API endpoint "/api/products" returns status 500
    # Когда пользователь пытается загрузить список продуктов
    # Тогда должен увидеть сообщение "Ошибка сервера"

  Сценарий: Использование русскоязычных шагов
    Когда мокирую GET запрос "/api/data" со статусом 200 и телом "success"
    # Ваши UI шаги
    # Когда пользователь запрашивает данные
    # Тогда должен получить ответ "success"

  Сценарий: POST с проверкой содержимого тела запроса
    Дано POST endpoint "/api/login" with body containing "username" returns status 200
    # Мок сработает только если тело POST запроса содержит "username"
    # Когда пользователь отправляет форму логина
    # Тогда должен успешно авторизоваться

  Сценарий: Проверка заголовков Authorization
    Дано API endpoint "/api/protected" with header "Authorization" containing "Bearer" returns status 200
    # Мок сработает только если есть заголовок Authorization с Bearer токеном
    # Когда пользователь обращается к защищенному ресурсу
    # Тогда должен получить доступ
