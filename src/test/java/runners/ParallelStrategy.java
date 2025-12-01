package runners;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * Кастомная стратегия параллельного выполнения Cucumber тестов.
 * 
 * Настройки:
 * - Parallelism: 4 потока
 * - Minimum runnable: 4
 * - Core pool size: 4
 * - Keep alive: 30 секунд
 * 
 * @author WireMock Integration
 * @version 1.0
 */
public class ParallelStrategy implements ParallelExecutionConfigurationStrategy {
    
    @Override
    public ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        return new ParallelExecutionConfiguration() {
            @Override
            public int getParallelism() {
                return 4; // Количество параллельных потоков
            }

            @Override
            public int getMinimumRunnable() {
                return 4;
            }

            @Override
            public int getMaxPoolSize() {
                return 4;
            }

            @Override
            public int getCorePoolSize() {
                return 4;
            }

            @Override
            public int getKeepAliveSeconds() {
                return 30;
            }
        };
    }
}
