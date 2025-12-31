package profect.goorm1.goormdotcom.common.observability.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.TracingAwareMeterObservationHandler;
import org.springframework.batch.core.configuration.annotation.BatchObservabilityBeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.observation.batch.BatchObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {

    /**
     * ObservationRegistry is Micrometer's central registry for observation configuration
     * (e.g. handlers, predicates, filters)
     * When this bean exists, Spring Boot auto-configures BatchObservabilityBeanPostProcessor.
     */

    @Bean
    public ObservationRegistry observationRegistry(MeterRegistry meterRegistry, Tracer tracer) {
        DefaultMeterObservationHandler observationHandler = new DefaultMeterObservationHandler(meterRegistry);
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        observationRegistry.observationConfig()
                .observationHandler(new TracingAwareMeterObservationHandler<>(observationHandler, tracer));
        return observationRegistry;
    }

    /**
     * The following bean declaration is for tracing signal of the batch job.
     *
     * If @EnableBatchProcessing or DefaultBatchConfiguration isn't used,
     * the following configuration must be set.
     *
     * https://docs.spring.io/spring-batch/reference/spring-batch-observability/micrometer.html#tracing
     */
//    @Bean
//    static BatchObservabilityBeanPostProcessor batchObservabilityBeanPostProcessor() {
//        return new BatchObservabilityBeanPostProcessor();
//    }

}
