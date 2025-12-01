package runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * JUnit 5 Test Runner для запуска Cucumber тестов.
 * 
 * Конфигурация для параллельного запуска:
 * - Parallel execution включен
 * - Стратегия: same_thread (каждый сценарий в своем потоке)
 * - Максимум потоков: 4
 * 
 * @author WireMock Integration
 * @version 1.0
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefs,hooks")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @skip")
// Настройки для параллельного выполнения
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "true")
@ConfigurationParameter(key = PARALLEL_CONFIG_STRATEGY_PROPERTY_NAME, value = "custom")
@ConfigurationParameter(key = PARALLEL_CONFIG_CUSTOM_CLASS_PROPERTY_NAME, value = "runners.ParallelStrategy")
public class CucumberTestRunner {
}
