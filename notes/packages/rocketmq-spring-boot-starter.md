# rocketmq-spring-boot-starter
```java
RocketMQAutoConfiguration
    + RocketMQProperties
    + DefaultMQProducer
    + DefaultLitePullConsumer
    + RocketMQTemplate
    MessageConverterConfiguration
        + RocketMQMessageConverter
    ListenerContainerConfiguration
        + @RocketMQMessageListener
        + RocketMQListener
        + RocketMQReplyListener
    ExtProducerResetConfiguration
        + @ExtRocketMQTemplateConfiguration
    ExtConsumerResetConfiguration
        + @ExtRocketMQConsumerConfiguration
    RocketMQTransactionConfiguration
        + @RocketMQTransactionListener
```
