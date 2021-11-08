- [1. spring-cloud-starter-alibaba-nacos-discovery](#1-spring-cloud-starter-alibaba-nacos-discovery)
- [2. spring-cloud-starter-alibaba-nacos-config](#2-spring-cloud-starter-alibaba-nacos-config)
- [3. spring-cloud-starter-dubbo](#3-spring-cloud-starter-dubbo)
- [4. dubbo-spring-boot-autoconfigure-*](#4-dubbo-spring-boot-autoconfigure-)
- [5. events](#5-events)

# 1. spring-cloud-starter-alibaba-nacos-discovery
```java
NacosServiceAutoConfiguration
    + NacosServiceManager
NacosDiscoveryAutoConfiguration
    + NacosDiscoveryProperties
    + NacosServiceDiscovery(NacosDiscoveryProperties, NacosServiceManager)
NacosDiscoveryEndpointAutoConfiguration
    + NacosDiscoveryEndpoint(NacosDiscoveryProperties, NacosServiceManager)
    + HealthIndicator(NacosDiscoveryProperties, NacosServiceManager)
NacosServiceRegistryAutoConfiguration
    + NacosServiceRegistry(NacosDiscoveryProperties)
    + NacosRegistration(NacosRegistrationCustomizer, NacosDiscoveryProperties)
    + NacosAutoServiceRegistration(NacosServiceRegistry, AutoServiceRegistrationProperties, NacosRegistration)
NacosDiscoveryClientConfiguration
    + DiscoveryClient(NacosServiceDiscovery)
    + NacosWatch(NacosServiceManager, NacosDiscoveryProperties, ThreadPoolTaskScheduler)
NacosReactiveDiscoveryClientConfiguration
    + NacosReactiveDiscoveryClient(NacosServiceDiscovery)
```

# 2. spring-cloud-starter-alibaba-nacos-config
```java
NacosConfigAutoConfiguration
    + NacosConfigProperties
    + NacosRefreshHistory
    + NacosConfigManager(NacosConfigProperties)
    + NacosContextRefresher(NacosConfigManager, NacosRefreshHistory)
NacosConfigEndpointAutoConfiguration
    + NacosConfigEndpoint(NacosConfigProperties, NacosRefreshHistory)
    + NacosConfigHealthIndicator
```

# 3. spring-cloud-starter-dubbo
```java
DubboMetadataAutoConfiguration
    + MetadataResolver(Contract)
DubboOpenFeignAutoConfiguration
    + TargeterBeanPostProcessor(
        Environment,
        DubboServiceMetadataRepository,
        DubboGenericServiceFactory,
        DubboGenericServiceExecutionContextFactory)
DubboServiceRegistrationAutoConfiguration
    + EurekaConfiguration$1
    + ConsulConfiguration$1
DubboServiceRegistrationNonWebApplicationAutoConfiguration
    + ZookeeperConfiguration$1
// setup RestTemplate bean
DubboLoadBalancedRestTemplateAutoConfiguration
DubboServiceAutoConfiguration
    + DubboGenericServiceFactory
    + DubboGenericServiceExecutionContextFactory
    + RequestParamServiceParameterResolver
    + RequestBodyServiceParameterResolver
    + RequestHeaderServiceParameterResolver
    + PathVariableServiceParameterResolver
DubboServiceDiscoveryAutoConfiguration
    + EurekaConfiguration$2
    + ConsulConfiguration$2
    + ZookeeperConfiguration$2
    + NacosConfiguration$1
DubboMetadataEndpointAutoConfiguration
    + DubboRestMetadataEndpoint
    + DubboDiscoveryEndpoint
    + DubboExportedURLsEndpoint
```

# 4. dubbo-spring-boot-autoconfigure-*
```java
DubboAutoConfiguration
    + DubboConfigurationProperties
```

# 5. events
```java
// register service implementations
DubboBootstrapApplicationListener.onContextRefreshedEvent
    DubboBootstrap.start
// register DubboMetadata
DubboServiceRegistrationAutoConfiguration.onDubboBootstrapStarted
    ServiceRegistry.register
```
