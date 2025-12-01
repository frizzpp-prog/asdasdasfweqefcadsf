package stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiremock.MockBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Готовые Cucumber Step Definitions для работы с WireMock моками.
 * 
 * Позволяет создавать моки прямо из feature-файлов без написания кода.
 * 
 * Примеры использования в feature файлах:
 * 
 * Given API endpoint "/api/users" returns status 200
 * Given API endpoint "/api/users" returns JSON: "{\"name\": \"John\"}"
 * Given API endpoint "/api/login" returns status 200 with JSON:
 *   """
 *   {"token": "abc123", "userId": 1}
 *   """
 * Given API endpoint "/api/slow" returns status 200 with 3000ms delay
 * When мокирую GET запрос "/api/data" со статусом 200 и телом "OK"
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class MockSteps {
    
    private static final Logger log = LoggerFactory.getLogger(MockSteps.class);
    
    // ==================== Простые моки ====================
    
    /**
     * Создает мок для GET запроса с указанным статусом.
     * 
     * Пример: Given API endpoint "/api/users" returns status 200
     */
    @Given("API endpoint {string} returns status {int}")
    public void apiEndpointReturnsStatus(String url, int status) {
        log.info("Создание мока: GET {} -> {}", url, status);
        MockBuilder.get(url)
            .withStatus(status)
            .stub();
    }
    
    /**
     * Создает мок для GET запроса с JSON телом ответа (inline).
     * 
     * Пример: Given API endpoint "/api/users" returns JSON: "{\"name\": \"John\"}"
     */
    @Given("API endpoint {string} returns JSON: {string}")
    public void apiEndpointReturnsJson(String url, String jsonBody) {
        log.info("Создание мока: GET {} -> JSON", url);
        MockBuilder.get(url)
            .withStatus(200)
            .withJsonBody(jsonBody)
            .stub();
    }
    
    /**
     * Создает мок для GET запроса с JSON телом ответа (DocString).
     * 
     * Пример:
     * Given API endpoint "/api/users" returns status 200 with JSON:
     *   """
     *   {"name": "John", "id": 1}
     *   """
     */
    @Given("API endpoint {string} returns status {int} with JSON:")
    public void apiEndpointReturnsStatusWithJson(String url, int status, String jsonBody) {
        log.info("Создание мока: GET {} -> {} JSON", url, status);
        MockBuilder.get(url)
            .withStatus(status)
            .withJsonBody(jsonBody)
            .stub();
    }
    
    /**
     * Создает мок для GET запроса с задержкой.
     * 
     * Пример: Given API endpoint "/api/slow" returns status 200 with 3000ms delay
     */
    @Given("API endpoint {string} returns status {int} with {int}ms delay")
    public void apiEndpointReturnsWithDelay(String url, int status, int delayMs) {
        log.info("Создание мока: GET {} -> {} (задержка {}ms)", url, status, delayMs);
        MockBuilder.get(url)
            .withStatus(status)
            .withDelay(delayMs)
            .stub();
    }
    
    /**
     * Создает мок для GET запроса с JSON из файла.
     * 
     * Пример: Given API endpoint "/api/users" returns JSON from file "responses/users.json"
     */
    @Given("API endpoint {string} returns JSON from file {string}")
    public void apiEndpointReturnsJsonFromFile(String url, String filePath) {
        log.info("Создание мока: GET {} -> JSON из файла {}", url, filePath);
        MockBuilder.get(url)
            .withStatus(200)
            .withJsonBodyFromFile(filePath)
            .stub();
    }
    
    // ==================== Моки для разных HTTP методов ====================
    
    /**
     * Создает мок для POST запроса.
     * 
     * Пример: Given POST endpoint "/api/login" returns status 200 with JSON: "{\"token\": \"abc\"}"
     */
    @Given("POST endpoint {string} returns status {int} with JSON: {string}")
    public void postEndpointReturnsJson(String url, int status, String jsonBody) {
        log.info("Создание мока: POST {} -> {} JSON", url, status);
        MockBuilder.post(url)
            .withStatus(status)
            .withJsonBody(jsonBody)
            .stub();
    }
    
    /**
     * Создает мок для POST запроса с DocString.
     * 
     * Пример:
     * Given POST endpoint "/api/login" returns status 200 with JSON:
     *   """
     *   {"token": "abc123"}
     *   """
     */
    @Given("POST endpoint {string} returns status {int} with JSON:")
    public void postEndpointReturnsStatusWithJson(String url, int status, String jsonBody) {
        log.info("Создание мока: POST {} -> {} JSON", url, status);
        MockBuilder.post(url)
            .withStatus(status)
            .withJsonBody(jsonBody)
            .stub();
    }
    
    /**
     * Создает мок для PUT запроса.
     * 
     * Пример: Given PUT endpoint "/api/users/1" returns status 200
     */
    @Given("PUT endpoint {string} returns status {int}")
    public void putEndpointReturns(String url, int status) {
        log.info("Создание мока: PUT {} -> {}", url, status);
        MockBuilder.put(url)
            .withStatus(status)
            .stub();
    }
    
    /**
     * Создает мок для DELETE запроса.
     * 
     * Пример: Given DELETE endpoint "/api/users/1" returns status 204
     */
    @Given("DELETE endpoint {string} returns status {int}")
    public void deleteEndpointReturns(String url, int status) {
        log.info("Создание мока: DELETE {} -> {}", url, status);
        MockBuilder.delete(url)
            .withStatus(status)
            .stub();
    }
    
    // ==================== Моки с условиями ====================
    
    /**
     * Создает мок для POST с проверкой содержимого тела запроса.
     * 
     * Пример: Given POST endpoint "/api/login" with body containing "username" returns status 200
     */
    @Given("POST endpoint {string} with body containing {string} returns status {int}")
    public void postWithBodyContaining(String url, String bodyContent, int status) {
        log.info("Создание мока: POST {} (тело содержит '{}') -> {}", url, bodyContent, status);
        MockBuilder.post(url)
            .withRequestBody(containing(bodyContent))
            .withStatus(status)
            .stub();
    }
    
    /**
     * Создает мок с проверкой заголовка.
     * 
     * Пример: Given API endpoint "/api/users" with header "Authorization" containing "Bearer" returns status 200
     */
    @Given("API endpoint {string} with header {string} containing {string} returns status {int}")
    public void apiWithHeaderContaining(String url, String headerName, String headerValue, int status) {
        log.info("Создание мока: GET {} (заголовок {} содержит '{}') -> {}", 
                 url, headerName, headerValue, status);
        MockBuilder.get(url)
            .withRequestHeader(headerName, containing(headerValue))
            .withStatus(status)
            .stub();
    }
    
    // ==================== Универсальный мок на русском ====================
    
    /**
     * Универсальный мок на русском языке.
     * 
     * Пример: When мокирую GET запрос "/api/data" со статусом 200 и телом "OK"
     */
    @When("мокирую GET запрос {string} со статусом {int} и телом {string}")
    public void мокируюGetЗапрос(String url, int status, String body) {
        log.info("Создание мока: GET {} -> {} с телом '{}'", url, status, body);
        MockBuilder.get(url)
            .withStatus(status)
            .withJsonBody(body)
            .stub();
    }
    
    /**
     * Мок для POST на русском языке.
     * 
     * Пример: When мокирую POST запрос "/api/login" со статусом 200
     */
    @When("мокирую POST запрос {string} со статусом {int}")
    public void мокируюPostЗапрос(String url, int status) {
        log.info("Создание мока: POST {} -> {}", url, status);
        MockBuilder.post(url)
            .withStatus(status)
            .stub();
    }

    // ==================== Проксирование ====================

    /**
     * Проксирует запросы на указанный URL.
     * 
     * Пример: Given API endpoint "/api/users" is proxied to "https://jsonplaceholder.typicode.com"
     */
    @Given("API endpoint {string} is proxied to {string}")
    public void apiEndpointIsProxiedTo(String url, String proxyBaseUrl) {
        log.info("Создание прокси: {} -> {}", url, proxyBaseUrl);
        MockBuilder.get(url)
            .proxiedFrom(proxyBaseUrl)
            .stub();
    }

    /**
     * Проксирует все запросы, соответствующие regex, на указанный URL.
     * 
     * Пример: Given all requests matching "/api/.*" are proxied to "https://api.example.com"
     */
    @Given("all requests matching {string} are proxied to {string}")
    public void allRequestsMatchingAreProxiedTo(String urlPattern, String proxyBaseUrl) {
        log.info("Создание прокси (regex): {} -> {}", urlPattern, proxyBaseUrl);
        MockBuilder.getMatching(urlPattern)
            .proxiedFrom(proxyBaseUrl)
            .stub();
    }
    
    /**
     * Настраивает fallback прокси на целевой стенд.
     * Все не перехваченные запросы будут проксироваться на указанный URL.
     * 
     * Пример: Given requests are proxied to "https://www.stend.ru"
     */
    @Given("requests are proxied to {string}")
    public void requestsAreProxiedTo(String targetUrl) {
        log.info("Настройка прокси fallback на: {}", targetUrl);
        MockBuilder.getMatching("/.*")
            .proxiedFrom(targetUrl)
            .stub();
    }
}
