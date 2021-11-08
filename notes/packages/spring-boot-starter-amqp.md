# spring-boot-starter-amqp
```java
RabbitAutoConfiguration
    + RabbitProperties
    RabbitAnnotationDrivenConfiguration
        + SimpleRabbitListenerContainerFactoryConfigurer
        // spring.rabbitmq.listener.type == simple
        + SimpleRabbitListenerContainerFactory
        + DirectRabbitListenerContainerFactoryConfigurer
        // spring.rabbitmq.listener.type == direct
        + DirectRabbitListenerContainerFactory
        @EnableRabbit
    RabbitConnectionFactoryCreator
        + CachingConnectionFactory
    RabbitTemplateConfiguration
        + RabbitTemplateConfigurer
        + RabbitTemplate
        // spring.rabbitmq.dynamic == true
        + AmqpAdmin
    MessagingTemplateConfiguration
        + RabbitMessagingTemplate
```
