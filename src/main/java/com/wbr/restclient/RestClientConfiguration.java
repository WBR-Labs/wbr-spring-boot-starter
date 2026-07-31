package com.wbr.restclient;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfiguration {

    @Value("${http.connect-timeout:5s}")
    private Duration connectTimeout;

    @Value("${http.read-timeout:30s}")
    private Duration readTimeout;

    @Bean
    public RestClient restClient(ObservationRegistry observationRegistry, RestClientLoggingInterceptor loggingInterceptor) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();

        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .requestFactory(factory)
                .observationRegistry(observationRegistry)
                .requestInterceptor(loggingInterceptor)
                .build();
    }
}
