package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiremock.WireMockManager;

/**
 * Cucumber хуки для автоматического управления жизненным циклом WireMock серверов.
 * 
 * Эти хуки обеспечивают:
 * - Автоматический запуск WireMock сервера перед каждым сценарием
 * - Автоматическую остановку WireMock сервера после каждого сценария
 * - Изоляцию между сценариями (каждый сценарий получает чистый сервер)
 * - Поддержку параллельного выполнения тестов
 * 
 * Использование:
 * Просто добавьте этот класс в package hooks, и он автоматически будет применяться
 * ко всем Cucumber сценариям.
 * 
 * Для выборочного применения используйте тег @wiremock на feature или сценарии:
 * @wiremock
 * Scenario: Мой сценарий с моками
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class WireMockHooks {
    
    private static final Logger log = LoggerFactory.getLogger(WireMockHooks.class);
    
    /**
     * Запускается перед каждым Cucumber сценарием.
     * 
     * Запускает новый WireMock сервер на свободном порту.
     * Базовый URL сервера можно получить через WireMockManager.getBaseUrl().
     * 
     * @param scenario текущий сценарий Cucumber
     */
    @Before(order = 0) // order = 0 чтобы запускался раньше других хуков
    public void startWireMock(Scenario scenario) {
        log.info("Запуск WireMock для сценария: {}", scenario.getName());
        
        String baseUrl;
        
        // Если сценарий помечен тегом @proxy - запускаем в режиме прокси
        if (scenario.getSourceTagNames().contains("@proxy")) {
            baseUrl = WireMockManager.startServerWithProxy();
            log.info("WireMock запущен в режиме HTTP прокси");
        } else {
            baseUrl = WireMockManager.startServer();
        }
        
        log.info("WireMock сервер доступен по адресу: {}", baseUrl);
        log.debug("ID сценария: {}, Теги: {}", scenario.getId(), scenario.getSourceTagNames());
    }
    
    /**
     * Запускается после каждого Cucumber сценария.
     * 
     * Останавливает WireMock сервер и освобождает ресурсы.
     * Это гарантирует, что каждый следующий сценарий получит чистый сервер.
     * 
     * @param scenario текущий сценарий Cucumber
     */
    @After(order = 1000) // order = 1000 чтобы выполнялся позже других хуков
    public void stopWireMock(Scenario scenario) {
        log.info("Остановка WireMock для сценария: {} (статус: {})", 
                 scenario.getName(), scenario.getStatus());
        
        // В случае ошибки можно добавить дополнительное логирование
        if (scenario.isFailed()) {
            log.warn("Сценарий завершился с ошибкой, WireMock сервер будет остановлен");
        }
        
        WireMockManager.stopServer();
    }
    
    /**
     * Опциональный хук для сброса маппингов между сценариями без перезапуска сервера.
     * 
     * Закомментирован по умолчанию. Раскомментируйте, если хотите переиспользовать
     * один и тот же сервер между сценариями (быстрее, но менее изолированно).
     */
    /*
    @Before(order = 1)
    public void resetWireMockMappings(Scenario scenario) {
        if (WireMockManager.isRunning()) {
            log.debug("Сброс маппингов WireMock для сценария: {}", scenario.getName());
            WireMockManager.resetMappings();
        }
    }
    */
}
