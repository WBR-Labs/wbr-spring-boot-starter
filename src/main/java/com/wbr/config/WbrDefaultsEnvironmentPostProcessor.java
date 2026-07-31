package com.wbr.config;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

// Merges wbr-defaults.yaml and wbr-defaults-{profile}.yaml from the starter JAR into the
// service's Environment. Spring Boot does not automatically merge YAML files that share a
// name across library JARs and the app's own classes directory — whichever comes first on
// the classpath wins and the other is silently ignored. That's why these files are NOT named
// application.yaml/application-{profile}.yaml. This processor runs after config data has
// loaded and adds the starter's defaults at lowest priority (base file last, so profile
// files still take precedence over it), so services can still override any value in their
// own config files.
public class WbrDefaultsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (String profile : environment.getActiveProfiles()) {
            load(environment, "wbr-defaults-" + profile + ".yaml");
        }
        load(environment, "wbr-defaults.yaml");
    }

    private void load(ConfigurableEnvironment environment, String filename) {
        Resource resource = new ClassPathResource(filename);
        if (!resource.exists()) {
            return;
        }
        try {
            List<PropertySource<?>> sources = loader.load(filename, resource);
            sources.forEach(source -> environment.getPropertySources().addLast(source));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load wbr starter defaults from " + filename, e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
