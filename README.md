# WBR Spring Boot Starter

Opinionated platform starter shared by all WBR microservices: logging, auth,
observability, error handling, HTTP client, graceful shutdown and health.

## Provides

- Web (REST controllers, `RestClient`, embedded server) + auto-configured `RestClient.Builder`
- Bearer-token authentication via OAuth2 resource server
- Bean Validation for request DTOs, driving standard 400 error handling
- Health, info, metrics and graceful-shutdown lifecycle (Actuator)
- Distributed tracing + metrics via OpenTelemetry, including a Logback bridge
- `@Cacheable`/`@EnableCaching` support backed by Caffeine
- JSON log encoding (Logstash encoder) for non-development profiles
- Swagger UI, enabled in the development profile only

## Usage

Add as a dependency (versions are managed via [wbr-parent](../wbr-parent)):

```xml
<dependency>
    <groupId>com.wbr</groupId>
    <artifactId>wbr-spring-boot-starter</artifactId>
</dependency>
```

---

Maintained by [WBR Technologies](https://wbrtechnologies.com).
