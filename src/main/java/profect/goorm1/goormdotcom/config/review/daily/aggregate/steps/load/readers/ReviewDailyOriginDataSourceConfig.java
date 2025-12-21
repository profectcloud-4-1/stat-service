package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers;

import org.springframework.batch.core.configuration.annotation.JobScope;
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

@JobScope
@Configuration
public class ReviewDailyOriginDataSourceConfig {

    @JobScope
    @ConfigurationProperties("spring.datasource.review.raw")
    @Bean
    public DataSourceProperties reviewRawDataSourceProperties() {
        return new DataSourceProperties();
    }

    @JobScope
    @Bean
    public DataSource reviewRawDataSource(DataSourceProperties reviewRawDataSourceProperties) {
        return reviewRawDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @JobScope
    @Bean
    @Profile("dev")
    public DataSourceInitializer reviewRawDataSourceInitializer(
            @Qualifier("reviewRawDataSource") DataSource reviewDataSource
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
