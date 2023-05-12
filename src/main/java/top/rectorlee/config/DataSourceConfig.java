package top.rectorlee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.rectorlee.util.DataSourceProperties;
import top.rectorlee.util.DynamicDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author Lee
 * @description
 * @date 2023-05-12  16:14:56
 */
@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dynamicDataSource(DataSourceProperties dataSourceProperties) {
        return  DynamicDataSourceFactory.buildDruidDataSource(dataSourceProperties);
    }
}
