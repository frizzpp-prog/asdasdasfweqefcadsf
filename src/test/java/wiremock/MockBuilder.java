package wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * Утилитарный класс для простого создания WireMock моков с использованием fluent API.
 * 
 * Предоставляет удобные методы для создания HTTP моков без необходимости
 * глубокого знания WireMock API.
 * 
 * Примеры использования:
 * 
 * // Простой GET запрос
 * MockBuilder.get("/api/users")
 *     .withStatus(200)
 *     .withJsonBody("{\"name\": \"John\"}")
 *     .stub();
 * 
 * // POST с проверкой тела запроса
 * MockBuilder.post("/api/login")
 *     .withRequestBody(containing("username"))
 *     .withStatus(200)
 *     .withJsonBodyFromFile("responses/login-success.json")
 *     .stub();
 * 
 * // Задержка ответа
 * MockBuilder.get("/api/slow")
 *     .withDelay(5000)
 *     .withStatus(408)
 *     .stub();
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class MockBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(MockBuilder.class);
    
    private final MappingBuilder mappingBuilder;
    private ResponseDefinitionBuilder responseBuilder;
    
    private MockBuilder(MappingBuilder mappingBuilder) {
        this.mappingBuilder = mappingBuilder;
        this.responseBuilder = aResponse();
    }
    
    // ==================== Создание моков для разных HTTP методов ====================
    
    /**
     * Создает мок для GET запроса.
     * 
     * @param url путь URL (например, "/api/users" или "/api/users/1")
     * @return builder для настройки мока
     */
    public static MockBuilder get(String url) {
        return new MockBuilder(WireMock.get(WireMock.urlEqualTo(url)));
    }
    
    /**
     * Создает мок для GET запроса с использованием regex паттерна.
     * 
     * @param urlPattern regex паттерн для URL (например, "/api/users/.*")
     * @return builder для настройки мока
     */
    public static MockBuilder getMatching(String urlPattern) {
        return new MockBuilder(WireMock.get(WireMock.urlMatching(urlPattern)));
    }
    
    /**
     * Создает мок для POST запроса.
     * 
     * @param url путь URL
     * @return builder для настройки мока
     */
    public static MockBuilder post(String url) {
        return new MockBuilder(WireMock.post(WireMock.urlEqualTo(url)));
    }
    
    /**
     * Создает мок для POST запроса с использованием regex паттерна.
     * 
     * @param urlPattern regex паттерн для URL
     * @return builder для настройки мока
     */
    public static MockBuilder postMatching(String urlPattern) {
        return new MockBuilder(WireMock.post(WireMock.urlMatching(urlPattern)));
    }
    
    /**
     * Создает мок для PUT запроса.
     * 
     * @param url путь URL
     * @return builder для настройки мока
     */
    public static MockBuilder put(String url) {
        return new MockBuilder(WireMock.put(WireMock.urlEqualTo(url)));
    }
    
    /**
     * Создает мок для DELETE запроса.
     * 
     * @param url путь URL
     * @return builder для настройки мока
     */
    public static MockBuilder delete(String url) {
        return new MockBuilder(WireMock.delete(WireMock.urlEqualTo(url)));
    }
    
    /**
     * Создает мок для PATCH запроса.
     * 
     * @param url путь URL
     * @return builder для настройки мока
     */
    public static MockBuilder patch(String url) {
        return new MockBuilder(WireMock.patch(WireMock.urlEqualTo(url)));
    }
    
    // ==================== Настройка ответа ====================
    
    /**
     * Устанавливает HTTP статус код ответа.
     * 
     * @param status статус код (например, 200, 404, 500)
     * @return текущий builder
     */
    public MockBuilder withStatus(int status) {
        this.responseBuilder.withStatus(status);
        return this;
    }
    
    /**
     * Устанавливает тело ответа в формате JSON.
     * 
     * Автоматически добавляет заголовок Content-Type: application/json
     * 
     * @param jsonBody JSON строка
     * @return текущий builder
     */
    public MockBuilder withJsonBody(String jsonBody) {
        this.responseBuilder
            .withHeader("Content-Type", "application/json")
            .withBody(jsonBody);
        return this;
    }
    
    /**
     * Устанавливает тело ответа в формате XML.
     * 
     * Автоматически добавляет заголовок Content-Type: application/xml
     * 
     * @param xmlBody XML строка
     * @return текущий builder
     */
    public MockBuilder withXmlBody(String xmlBody) {
        this.responseBuilder
            .withHeader("Content-Type", "application/xml")
            .withBody(xmlBody);
        return this;
    }
    
    /**
     * Устанавливает тело ответа из файла с JSON контентом.
     * 
     * Файл должен находиться в src/test/resources или по абсолютному пути.
     * 
     * @param filePath путь к файлу (например, "responses/user.json")
     * @return текущий builder
     */
    public MockBuilder withJsonBodyFromFile(String filePath) {
        String content = readFileContent(filePath);
        return withJsonBody(content);
    }
    
    /**
     * Устанавливает тело ответа из файла.
     * 
     * @param filePath путь к файлу
     * @param contentType Content-Type заголовок
     * @return текущий builder
     */
    public MockBuilder withBodyFromFile(String filePath, String contentType) {
        String content = readFileContent(filePath);
        this.responseBuilder
            .withHeader("Content-Type", contentType)
            .withBody(content);
        return this;
    }
    
    /**
     * Добавляет заголовок к ответу.
     * 
     * @param name имя заголовка
     * @param value значение заголовка
     * @return текущий builder
     */
    public MockBuilder withHeader(String name, String value) {
        this.responseBuilder.withHeader(name, value);
        return this;
    }
    
    /**
     * Добавляет задержку перед отправкой ответа.
     * 
     * Полезно для тестирования таймаутов и медленных соединений.
     * 
     * @param delayMs задержка в миллисекундах
     * @return текущий builder
     */
    public MockBuilder withDelay(int delayMs) {
        this.responseBuilder.withFixedDelay(delayMs);
        return this;
    }

    /**
     * Настраивает проксирование запроса на другой базовый URL.
     * 
     * @param baseUrl базовый URL, куда перенаправлять запросы (например, "https://api.github.com")
     * @return текущий builder
     */
    public MockBuilder proxiedFrom(String baseUrl) {
        this.responseBuilder.proxiedFrom(baseUrl);
        return this;
    }
    
    // ==================== Настройка условий запроса ====================
    
    /**
     * Добавляет условие на тело запроса.
     * 
     * @param bodyPattern паттерн для проверки тела (например, containing("username"))
     * @return текущий builder
     */
    public MockBuilder withRequestBody(StringValuePattern bodyPattern) {
        this.mappingBuilder.withRequestBody(bodyPattern);
        return this;
    }
    
    /**
     * Добавляет условие на заголовок запроса.
     * 
     * @param name имя заголовка
     * @param valuePattern паттерн для проверки значения
     * @return текущий builder
     */
    public MockBuilder withRequestHeader(String name, StringValuePattern valuePattern) {
        this.mappingBuilder.withHeader(name, valuePattern);
        return this;
    }
    
    /**
     * Добавляет условие на query параметр.
     * 
     * @param name имя параметра
     * @param valuePattern паттерн для проверки значения
     * @return текущий builder
     */
    public MockBuilder withQueryParam(String name, StringValuePattern valuePattern) {
        this.mappingBuilder.withQueryParam(name, valuePattern);
        return this;
    }
    
    // ==================== Регистрация мока ====================
    
    /**
     * Регистрирует мок в WireMock сервере.
     * 
     * После вызова этого метода мок становится активным и будет
     * перехватывать подходящие HTTP запросы.
     */
    public void stub() {
        this.mappingBuilder.willReturn(this.responseBuilder);
        stubFor(this.mappingBuilder);
        log.debug("Зарегистрирован новый мок в WireMock");
    }
    
    // ==================== Вспомогательные методы ====================
    
    /**
     * Читает содержимое файла.
     * 
     * Сначала ищет файл в resources, затем пытается прочитать как абсолютный путь.
     * 
     * @param filePath путь к файлу
     * @return содержимое файла
     */
    private String readFileContent(String filePath) {
        try {
            // Пытаемся найти файл в resources
            var resourceUrl = getClass().getClassLoader().getResource(filePath);
            if (resourceUrl != null) {
                Path path = Paths.get(resourceUrl.toURI());
                return Files.readString(path);
            }
            
            // Пытаемся прочитать как абсолютный путь
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readString(path);
            }
            
            throw new IllegalArgumentException("Файл не найден: " + filePath);
            
        } catch (IOException | java.net.URISyntaxException e) {
            log.error("Ошибка при чтении файла: {}", filePath, e);
            throw new RuntimeException("Не удалось прочитать файл: " + filePath, e);
        }
    }
}
