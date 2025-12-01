# language: ru
@proxy
Функция: Тестирование UI на удаленном стенде через WireMock прокси

  Предыстория:
    # WireMock автоматически запускается в режиме прокси через WireMockHooks
    # Браузер настраивается через ProxyHooks (требуется адаптация к вашей инфраструктуре)
    Дано requests are proxied to "https://www.stend.ru"

  Сценарий: Подмена ответа /api/step1 при нажатии кнопки
    # Перехватываем конкретный endpoint и подменяем ответ
    Дано API endpoint "/api/step1" returns status 200 with JSON:
      """
      {
        "status": "success",
        "message": "Это подмененный ответ от WireMock",
        "nextStep": "/api/step2"
      }
      """
    
    # НЕ РЕАЛИЗОВАНО:  Steps для работы с браузером
    # Требуется адаптация ProxyHooks.java к вашей инфраструктуре
    # Когда открываю страницу "https://www.stend.ru/test-page"
    # И нажимаю кнопку "Start Process"
    # Тогда вижу текст " success"
  
  # ========== Пример с публичным API для демонстрации ==========
  
  @demo
  Сценарий: Демо - подмена ответа публичного API
    # Используем публичный API для демонстрации работы прокси
    Дано requests are proxied to "https://jsonplaceholder.typicode.com"
    
    # Подменяем ответ для /posts/1
    Дано API endpoint "/posts/1" returns status 200 with JSON:
      """
      {
        "userId": 1,
        "id": 1,
        "title": "ПОДМЕННЫЙ ЗАГОЛОВОК ОТ WIREMOCK",
        "body": "Это подмененное тело поста"
      }
      """
    
    # Остальные запросы (например /posts/2, /users/1) будут проксироваться
    # на реальный jsonplaceholder.typicode.com
