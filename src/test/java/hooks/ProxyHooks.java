package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiremock.WireMockManager;

/**
 * Hook для настройки браузера с WireMock прокси.
 * 
 * Активируется автоматически для сценариев с тегом @proxy.
 * Настраивает Selenium WebDriver на использование WireMock как HTTP прокси.
 * 
 * ВАЖНО: Этот класс предоставляет пример настройки прокси.
 * Вам нужно адаптировать метод setupBrowserProxy() под вашу существующую
 * инфраструктуру создания WebDriver.
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class ProxyHooks {
    
    private static final Logger log = LoggerFactory.getLogger(ProxyHooks.class);
    
    /**
     * Настраивает браузер на использование WireMock прокси.
     * 
     * Выполняется после запуска WireMock сервера (order = 1).
     * Активируется только для сценариев с тегом @proxy.
     */
    @Before(value = "@proxy", order = 1)
    public void setupBrowserProxy(Scenario scenario) {
        int port = WireMockManager.getPort();
        
        if (port == 0) {
            log.warn("WireMock сервер не запущен, пропускаем настройку прокси");
            return;
        }
        
        log.info("Настройка браузера на использование WireMock прокси: localhost:{}", port);
        
        // Создание Selenium Proxy объекта
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("localhost:" + port);
        proxy.setSslProxy("localhost:" + port);
        
        // TODO: АДАПТИРОВАТЬ ПОД ВАШУ ИНФРАСТРУКТУРУ
        // 
        // Примеры настройки для разных браузеров:
        //
        // Для Chrome:
        // ChromeOptions options = new ChromeOptions();
        // options.setProxy(proxy);
        // WebDriver driver = new ChromeDriver(options);
        //
        // Для Firefox:
        // FirefoxOptions options = new FirefoxOptions();
        // options.setProxy(proxy);
        // WebDriver driver = new FirefoxDriver(options);
        //
        // Если у вас уже есть ThreadLocal driver или DriverManager,
        // вам нужно передать proxy в опции при создании драйвера.
        
        log.debug("Proxy настроен: HTTP={}, SSL={}", proxy.getHttpProxy(), proxy.getSslProxy());
    }
    
    /**
     * Вспомогательный метод для создания ChromeOptions с прокси.
     * 
     * @param proxyHost хост прокси (обычно "localhost")
     * @param proxyPort порт прокси
     * @return настроенный ChromeOptions
     */
    public static ChromeOptions createChromeOptionsWithProxy(String proxyHost, int proxyPort) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyHost + ":" + proxyPort);
        proxy.setSslProxy(proxyHost + ":" + proxyPort);
        
        ChromeOptions options = new ChromeOptions();
        options.setProxy(proxy);
        
        return options;
    }
    
    /**
     * Вспомогательный метод для создания FirefoxOptions с прокси.
     * 
     * @param proxyHost хост прокси (обычно "localhost")
     * @param proxyPort порт прокси
     * @return настроенный FirefoxOptions
     */
    public static FirefoxOptions createFirefoxOptionsWithProxy(String proxyHost, int proxyPort) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyHost + ":" + proxyPort);
        proxy.setSslProxy(proxyHost + ":" + proxyPort);
        
        FirefoxOptions options = new FirefoxOptions();
        options.setProxy(proxy);
        
        return options;
    }
}
