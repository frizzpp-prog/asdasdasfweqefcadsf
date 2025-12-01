package wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Менеджер для управления WireMock серверами с поддержкой параллельного выполнения тестов.
 * 
 * Использует ThreadLocal для изоляции экземпляров серверов между потоками,
 * что обеспечивает безопасную работу при параллельном запуске тестов.
 * 
 * Основные возможности:
 * - Автоматическое выделение свободных портов
 * - Изоляция серверов между потоками (thread-safe)
 * - Простое API для управления жизненным циклом
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class WireMockManager {
    
    private static final Logger log = LoggerFactory.getLogger(WireMockManager.class);
    
    /**
     * ThreadLocal хранилище для WireMock серверов.
     * Каждый поток получает свой собственный экземпляр сервера.
     */
    private static final ThreadLocal<WireMockServer> WIREMOCK_SERVER = new ThreadLocal<>();
    
    /**
     * Запускает новый WireMock сервер на автоматически выбранном свободном порту.
     * 
     * Если сервер уже запущен в текущем потоке, он будет остановлен и запущен заново.
     * Сервер автоматически сбрасывает все маппинги при старте.
     * 
     * @return базовый URL запущенного WireMock сервера (например, "http://localhost:8080")
     */
    public static String startServer() {
        stopServer(); // Останавливаем предыдущий сервер, если был
        
        int port = findFreePort();
        
        WireMockServer server = new WireMockServer(
            WireMockConfiguration.options()
                .port(port)
                .dynamicPort() // Fallback на динамический порт если указанный занят
        );
        
        server.start();
        WIREMOCK_SERVER.set(server);
        
        // Настраиваем статический DSL для текущего потока
        WireMock.configureFor("localhost", server.port());
        
        String baseUrl = server.baseUrl();
        log.info("WireMock сервер запущен: {} (поток: {})", baseUrl, Thread.currentThread().getName());
        
        return baseUrl;
    }
    
    /**
     * Запускает WireMock сервер в режиме HTTP прокси.
     * 
     * В этом режиме WireMock может:
     * - Перехватывать HTTP/HTTPS запросы от браузера
     * - Подменять ответы для stubbed endpoints
     * - Проксировать остальные запросы на реальные серверы
     * 
     * Используется для тестирования UI на удаленных стендах.
     * 
     * @return базовый URL запущенного WireMock сервера
     */
    public static String startServerWithProxy() {
        stopServer();
        
        int port = findFreePort();
        
        WireMockServer server = new WireMockServer(
            WireMockConfiguration.options()
                .port(port)
                .dynamicPort()
                .enableBrowserProxying(true) // Включаем режим HTTP прокси
        );
        
        server.start();
        WIREMOCK_SERVER.set(server);
        
        WireMock.configureFor("localhost", server.port());
        
        String baseUrl = server.baseUrl();
        log.info("WireMock сервер запущен в режиме прокси: {} (поток: {})", 
                 baseUrl, Thread.currentThread().getName());
        
        return baseUrl;
    }
    
    /**
     * Останавливает WireMock сервер в текущем потоке.
     * 
     * Если сервер не был запущен, метод ничего не делает.
     * После остановки экземпляр сервера удаляется из ThreadLocal.
     */
    public static void stopServer() {
        WireMockServer server = WIREMOCK_SERVER.get();
        
        if (server != null && server.isRunning()) {
            log.info("Остановка WireMock сервера на порту: {} (поток: {})", 
                     server.port(), Thread.currentThread().getName());
            server.stop();
            WIREMOCK_SERVER.remove();
        }
    }
    
    /**
     * Возвращает экземпляр WireMock сервера для текущего потока.
     * 
     * @return текущий WireMock сервер или null, если сервер не запущен
     */
    public static WireMockServer getServer() {
        return WIREMOCK_SERVER.get();
    }
    
    /**
     * Возвращает базовый URL WireMock сервера для текущего потока.
     * 
     * @return базовый URL (например, "http://localhost:8080") или null, если сервер не запущен
     */
    public static String getBaseUrl() {
        WireMockServer server = getServer();
        return server != null ? server.baseUrl() : null;
    }
    
    /**
     * Сбрасывает все маппинги (моки) на сервере в текущем потоке.
     * 
     * Полезно для очистки состояния между тестовыми сценариями.
     * Если сервер не запущен, метод ничего не делает.
     */
    public static void resetMappings() {
        WireMockServer server = getServer();
        
        if (server != null && server.isRunning()) {
            server.resetAll();
            log.debug("Сброшены все маппинги WireMock (поток: {})", Thread.currentThread().getName());
        }
    }
    
    /**
     * Проверяет, запущен ли WireMock сервер в текущем потоке.
     * 
     * @return true если сервер запущен, иначе false
     */
    public static boolean isRunning() {
        WireMockServer server = getServer();
        return server != null && server.isRunning();
    }
    
    /**
     * Возвращает порт WireMock сервера для текущего потока.
     * 
     * Используется для настройки Selenium WebDriver на использование WireMock как прокси.
     * 
     * @return номер порта или 0, если сервер не запущен
     */
    public static int getPort() {
        WireMockServer server = getServer();
        return server != null ? server.port() : 0;
    }
    
    /**
     * Находит свободный порт в системе.
     * 
     * Использует ServerSocket для автоматического поиска свободного порта.
     * 
     * @return номер свободного порта
     */
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            log.error("Ошибка при поиске свободного порта", e);
            throw new RuntimeException("Не удалось найти свободный порт для WireMock", e);
        }
    }
}
