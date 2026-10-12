# Spring Configuration Scenarios

A small Spring Boot 3.5 / Java 21 service where every configuration feature solves a problem a real
service has. Each scenario has a test that **proves** the behavior (not just that the context loads).

```bash
mvn test                                   # run all scenario tests
mvn spring-boot:run                        # dev profile (the default): sandbox payments, in-memory cache
PAYMENTS_API_KEY=sk_live_x mvn spring-boot:run -Dspring-boot.run.profiles=prod
curl -H 'X-Tenant-Id: acme' localhost:8080/orders/42
```

On startup, `WiringSummaryRunner` logs which implementation was chosen for each decision, so you can
switch profiles/properties and watch the wiring change.

## Feature → scenario map

| Feature | Real-world scenario | Where | Proven by |
|---|---|---|---|
| **Multiple implementations** | | | |
| `@Primary` | Receipts go out on the default channel (email) without naming it | `notifications/EmailNotificationSender`, `ReceiptService` | `NotificationWiringTest` |
| Custom `@Qualifier` annotation | OTP codes must always use SMS; the qualifier names the *role*, so changing provider moves one annotation | `notifications/OtpChannel`, `OtpService` | `NotificationWiringTest` |
| `List<T>` injection + `@Order` | Router sends on the customer's preferred channel, falls back to highest priority | `notifications/NotificationRouter` | `NotificationWiringTest` |
| **Conditional beans** | | | |
| `@ConditionalOnProperty` (feature flag) | Push notifications only where enabled (prod) | `notifications/PushNotificationSender` | `NotificationWiringTest` |
| `@ConditionalOnProperty` (backend switch) | Cache backend chosen by `platform.cache.type` | `platform/cache/CacheAutoConfiguration` | `CacheAutoConfigurationTest` |
| `@ConditionalOnMissingBean` | Company "platform starter" provides defaults; any service can override with its own bean | same | `CacheAutoConfigurationTest` |
| `@ConditionalOnClass` / `OnMissingClass` | JSON serializer when Jackson is on the classpath, JDK serialization otherwise | same | `CacheAutoConfigurationTest` (uses `FilteredClassLoader`) |
| Custom `@Conditional` | GDPR audit trail only in EU/UK deployments; optional consumption via `ObjectProvider` | `compliance/ConditionalOnRegion`, `OnRegionCondition`, `CustomerProfileService` | `RegionConditionTest` |
| **Profiles** | | | |
| `@Profile` + expressions | Live payment gateway only in `prod & !load-test`; load tests use prod config but never charge real cards | `payments/PaymentConfig` | `PaymentProfilesTest` |
| Profile-specific YAML | `application-prod.yml` flips gateway URL, Redis cache and push in one switch; secrets from env vars | `resources/application-*.yml` | `ProdProfileIntegrationTest` |
| Fail-fast validation | Prod refuses to start without `PAYMENTS_API_KEY` instead of failing on the first checkout | `payments/PaymentConfig` | `PaymentProfilesTest` |
| `spring.profiles.default` | Bare `mvn spring-boot:run` is a dev run | `application.yml` | startup log (`profiles: [dev] (default)`) |
| **Bean scopes** | | | |
| Singleton (default) | Stateless services, shared connection pool | everywhere | — |
| Prototype + `ObjectProvider` | Stateful, single-use CSV import job per upload | `imports/CsvImportJob`, `ImportService` | `ImportScopeTest` (also shows the prototype-in-singleton trap) |
| Request scope + scoped proxy | Correlation id / tenant per HTTP request, read by a singleton audit logger | `web/RequestContext`, `AuditLogger` | `RequestScopeTest` |
| Custom scope (`thread`) | Per-thread instance of non-thread-safe `DecimalFormat` | `formatting/ThreadScopeConfig`, `CurrencyFormatter` | `ThreadScopeTest` |
| **Configuration class processing** | | | |
| Full vs lite mode (`proxyBeanMethods`) | Two HTTP clients must share ONE connection pool | `http/HttpClientConfig` | `HttpClientConfigTest` |
| `static @Bean` for a `BeanFactoryPostProcessor` | Registering the custom scope before any bean is created | `formatting/ThreadScopeConfig` | `ThreadScopeTest` |
| `@Enable*` + `@Import` + `ImportSelector` | Opt-in transactional-outbox module; annotation attribute picks JDBC or in-memory storage | `outbox/EnableOutbox`, `OutboxImportSelector` | `EnableOutboxTest` |
| Auto-configuration ordering | Platform defaults evaluated after app config (`AutoConfiguration.imports`, `afterName` Jackson) | `platform/cache/CacheAutoConfiguration` | `CacheAutoConfigurationTest` |
| **Controlling instantiation order** | | | |
| `@DependsOn` | Reference-data cache must load only after schema migration (dependency invisible to the container) | `startup/ReferenceDataCache`, `SchemaMigrator` | `InstantiationOrderTest` (shows failure without it) |
| `@Lazy` (class + injection point) | Expensive reporting engine built on first use, not at startup | `startup/ReportingEngine`, `MonthlyReportService` | `StartupSequenceTest` |
| `SmartLifecycle` phases | Message consumer starts last and stops first; outbox relay outlives it | `startup/OrderEventConsumer`, `OutboxRelay` | `InstantiationOrderTest` |
| `ApplicationRunner` + `@Order` | Smoke check, then wiring summary, after everything is up | `startup/SmokeCheckRunner`, `WiringSummaryRunner` | `StartupSequenceTest` |

## Gotchas the code calls out

- **`@ConditionalOnMissingBean` belongs in auto-configuration.** In regular `@Configuration`/`@Component`
  classes the result depends on registration order. Here the conditional defaults live in a separate
  package (not component-scanned) and are registered through `AutoConfiguration.imports`.
- **Don't rely on `@Bean` method order for mutually exclusive beans.** The two cache backends use
  complementary property values; the two serializers use `OnClass` / `OnMissingClass`.
- **A prototype injected into a singleton is created once.** Use `ObjectProvider`, `@Lookup` or `Provider<T>`.
- **`@Lazy` on a class is undone by an eager consumer.** Put `@Lazy` on the injection point too.
- **Classes reached only via `ImportSelector` must not be `@Configuration`** if they live in a scanned
  package, or scanning registers them anyway (`outbox/OutboxConfigurations`).
- **`SimpleThreadScope` never calls destroy callbacks**, and thread scope buys nothing with virtual threads.

## Layout

```
src/main/java/com/example/
  platform/cache/      library-style auto-configuration (not component-scanned)
  scenarios/
    notifications/     multiple implementations
    compliance/        custom condition
    payments/          profiles
    imports/ web/ formatting/   prototype, request, custom thread scope
    http/ outbox/      configuration class processing
    startup/           instantiation order and lifecycle
```

Infrastructure (Redis, payment provider, database) is simulated in-process so the project runs with no
external services; each simulated class says where the real call would go.
