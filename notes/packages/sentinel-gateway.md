# spring-cloud-alibaba-sentinel-gateway
```java
SentinelSCGAutoConfiguration
    + SentinelGatewayProperties
    + SentinelGatewayBlockExceptionHandler
    + SentinelGatewayFilter
SentinelGatewayAutoConfiguration
    SentinelConverterConfiguration
        SentinelJsonConfiguration
        SentinelXmlConfiguration
```
# spring-cloud-alibaba-sentinel
```java
SentinelWebAutoConfiguration
    + SentinelWebMvcConfig
      + SentinelWebInterceptor
SentinelWebFluxAutoConfiguration
    + SentinelBlockExceptionHandler
    + SentinelWebFluxFilter
SentinelEndpointAutoConfiguration
    + SentinelEndpoint
    + SentinelHealthIndicator
SentinelAutoConfiguration
    SentinelConverterConfiguration
        SentinelJsonConfiguration
        SentinelXmlConfiguration
    + SentinelResourceAspect
    + SentinelBeanPostProcessor(ApplicationContext)
    + SentinelDataSourceHandler(DefaultListableBeanFactory, SentinelProperties, Environment)
SentinelFeignAutoConfiguration
    + Feign.Builder
```
# spring-cloud-circuitbreaker-sentinel
```java
SentinelCircuitBreakerAutoConfiguration
    + CircuitBreakerFactory
    + SentinelCustomizerConfiguration
ReactiveSentinelCircuitBreakerAutoConfiguration
    + ReactiveCircuitBreakerFactory
    + ReactiveSentinelCustomizerConfiguration
```
