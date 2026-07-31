package com.wbr.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// The OTEL appender in logback-spring.xml is wired by Logback's own XML parser, outside
// Spring's DI, so it has no way to receive the OpenTelemetry SDK bean on its own. This installs
// it once the context is otherwise fully initialized, which is what actually turns the appender
// on - without this call it silently drops every log event instead of exporting it.
//
// Deliberately no @ConditionalOnBean(OpenTelemetry.class) here: this class is picked up by
// component scanning (not spring.factories/AutoConfiguration.imports), which runs before
// Boot's deferred auto-configuration phase registers the OpenTelemetry bean definition. The
// condition would see no candidate yet and silently skip this whole class. Boot's
// OpenTelemetrySdkAutoConfiguration always provides some OpenTelemetry bean (real or a no-op
// fallback), so plain @Bean parameter injection - resolved later, at actual bean creation
// time - is both sufficient and correct.
@Configuration
public class OpenTelemetryLoggingConfiguration {

    @Bean
    SmartInitializingSingleton openTelemetryAppenderInstaller(OpenTelemetry openTelemetry) {
        return () -> OpenTelemetryAppender.install(openTelemetry);
    }
}
