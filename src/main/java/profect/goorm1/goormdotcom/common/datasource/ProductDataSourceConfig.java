package profect.goorm1.goormdotcom.common.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
@Profile("ds-product")
public class ProductDataSourceConfig {

    @ConfigurationProperties("app.datasource.product")
    @Bean
    public DataSourceProperties productDataSourceProperties() { return new DataSourceProperties(); }

    @Bean
    public DataSource productDataSource(DataSourceProperties productDataSourceProperties) {
        return productDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Profile("dev")
    public DataSourceInitializer reviewRawDataSourceInitializer(
            @Qualifier("productDataSource") DataSource reviewDataSource
    ) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/review-raw/schema.sql"));
        populator.addScript(new ClassPathResource("db/review-raw/data.sql"));

        populator.setContinueOnError(false);
        populator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(reviewDataSource);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }
}
