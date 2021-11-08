# spring-cloud-gateway-server
```java
GatewayClassPathWarningAutoConfiguration
    throw new MvcFoundOnClasspathException
GatewayAutoConfiguration
    // routes
    + RouteLocatorBuilder
    + PropertiesRouteDefinitionLocator
    + InMemoryRouteDefinitionRepository
    + RouteDefinitionLocator
    + RouteLocator RouteDefinitionRouteLocator
    + RouteLocator CachingRouteLocator
    // handler
    + RoutePredicateHandlerMapping
      + FilteringWebHandler
    // header filters
    + ForwardedHeadersFilter
    + RemoveHopByHopHeadersFilter
    + XForwardedHeadersFilter
    // global filters
    + AdaptCachedBodyGlobalFilter
    + RemoveCachedBodyFilter
    + RouteToRequestUrlFilter
    + ForwardRoutingFilter
    + ForwardPathFilter
    + WebsocketRoutingFilter
    // web filters
    + WeightCalculatorWebFilter
    // gateway filters
    + AddRequestHeaderGatewayFilterFactory
    + MapRequestHeaderGatewayFilterFactory
    + AddRequestParameterGatewayFilterFactory
    + AddResponseHeaderGatewayFilterFactory
    + ModifyRequestBodyGatewayFilterFactory
    + DedupeResponseHeaderGatewayFilterFactory
    + ModifyResponseBodyGatewayFilterFactory
    + PrefixPathGatewayFilterFactory
    + PreserveHostHeaderGatewayFilterFactory
    + RedirectToGatewayFilterFactory
    + RemoveRequestHeaderGatewayFilterFactory
    + RemoveRequestParameterGatewayFilterFactory
    + RemoveResponseHeaderGatewayFilterFactory
    + RequestRateLimiterGatewayFilterFactory
        + PrincipalNameKeyResolver
    + RewritePathGatewayFilterFactory
    + RetryGatewayFilterFactory
    + SetPathGatewayFilterFactory
    + SecureHeadersGatewayFilterFactory
    + SetRequestHeaderGatewayFilterFactory
    + SetRequestHostHeaderGatewayFilterFactory
    + SetResponseHeaderGatewayFilterFactory
    + RewriteResponseHeaderGatewayFilterFactory
    + RewriteLocationResponseHeaderGatewayFilterFactory
    + SetStatusGatewayFilterFactory
    + SaveSessionGatewayFilterFactory
    + StripPrefixGatewayFilterFactory
    + RequestHeaderToRequestUriGatewayFilterFactory
    + RequestSizeGatewayFilterFactory
    + RequestHeaderSizeGatewayFilterFactory
    // gateway predicates
    + AfterRoutePredicateFactory
    + BeforeRoutePredicateFactory
    + BetweenRoutePredicateFactory
    + CookieRoutePredicateFactory
    + HeaderRoutePredicateFactory
    + HostRoutePredicateFactory
    + MethodRoutePredicateFactory
    + PathRoutePredicateFactory
    + QueryRoutePredicateFactory
    + ReadBodyRoutePredicateFactory
    + RemoteAddrRoutePredicateFactory
    + WeightRoutePredicateFactory
    + CloudFoundryRouteServiceRoutePredicateFactory
    // converts
    + StringToZonedDateTimeConverter
    // properties
    + GlobalCorsProperties
    + GatewayProperties
    + SecureHeadersProperties
    // others
    + ConfigurationService
    + RouteRefreshListener
    + WebSocketService
    + GzipMessageBodyResolver
    NettyConfiguration
    GatewayActuatorConfiguration
GatewayResilience4JCircuitBreakerAutoConfiguration
GatewayNoLoadBalancerClientAutoConfiguration
    + NoLoadBalancerClientFilter
GatewayMetricsAutoConfiguration
	+ GatewayHttpTagsProvider
	+ GatewayRouteTagsProvider
	+ PropertiesTagsProvider(GatewayMetricsProperties)
	+ GatewayMetricsFilter(MeterRegistry, List<GatewayTagsProvider>, GatewayMetricsProperties)
GatewayRedisAutoConfiguration
    + RedisScript
    + RedisRateLimiter(ReactiveStringRedisTemplate, RedisScript<List<Long>>, ConfigurationService)
GatewayDiscoveryClientAutoConfiguration
    + DiscoveryLocatorProperties
    + DiscoveryClientRouteDefinitionLocator(ReactiveDiscoveryClient, DiscoveryLocatorProperties)
SimpleUrlHandlerMappingGlobalCorsAutoConfiguration
GatewayReactiveLoadBalancerClientAutoConfiguration
	+ ReactiveLoadBalancerClientFilter(LoadBalancerClientFactory, GatewayLoadBalancerProperties, LoadBalancerProperties)
	+ LoadBalancerServiceInstanceCookieFilter(LoadBalancerProperties)
GatewayReactiveOAuth2AutoConfiguration
    + ReactiveOAuth2AuthorizedClientManager(ReactiveClientRegistrationRepository, ServerOAuth2AuthorizedClientRepository)
```

# spring-webflux
```java
HttpHandlerAutoConfiguration
    + HttpHandler(ObjectProvider<WebFluxProperties>)
ReactiveWebServerFactoryAutoConfiguration
    ReactiveWebServerFactoryConfiguration.EmbeddedNetty
    + ReactiveWebServerFactoryCustomizer(ServerProperties)
    + TomcatReactiveWebServerFactoryCustomizer(ServerProperties)
    + ForwardedHeaderTransformer
WebFluxAutoConfiguration
    WelcomePageConfiguration
    WebFluxConfig
        EnableWebFluxConfiguration extends DelegatingWebFluxConfiguration extends WebFluxConfigurationSupport
        + DispatcherHandler
        + WebExceptionHandler
        + RequestMappingHandlerMapping
        + RequestedContentTypeResolver
        + RouterFunctionMapping
        + HandlerMapping
        + ResourceUrlProvider
        + RequestMappingHandlerAdapter
        + ServerCodecConfigurer
        + LocaleContextResolver
        + FormattingConversionService
        + ReactiveAdapterRegistry
        + Validator
        + HandlerFunctionAdapter
        + SimpleHandlerAdapter
        + WebSocketHandlerAdapter
        + ResponseEntityResultHandler
        + ResponseBodyResultHandler
        + ViewResolutionResultHandler
        + ServerResponseResultHandler
ErrorWebFluxAutoConfiguration
ClientHttpConnectorAutoConfiguration
    ClientHttpConnectorConfiguration.ReactorNetty
    + WebClientCustomizer(ClientHttpConnector)
WebClientAutoConfiguration
```
