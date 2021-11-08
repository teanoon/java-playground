# spring-kafka
```java
KafkaAutoConfiguration
    + KafkaProperties
    + KafkaTemplate
    + ProducerListener
    + ConsumerFactory
    + ProducerFactory
    + KafkaTransactionManager
    + KafkaJaasLoginModuleInitializer
    + KafkaAdmin
    KafkaAnnotationDrivenConfiguration
        + ConcurrentKafkaListenerContainerFactoryConfigurer
        + ConcurrentKafkaListenerContainerFactory
    KafkaStreamsAnnotationDrivenConfiguration
        + KafkaStreamsConfiguration
        + KafkaStreamsFactoryBeanConfigurer
    EnableKafka
```

# Transaction
```java
KafkaTemplate.send(String topic, @Nullable V data)
    getTheProducer(String topic)
        DefaultKafkaProducerFactory.createTransactionalProducer(String txIdPrefixArg)
            Producer.initTransactions()
        ProducerFactoryUtils.getTransactionalResourceHolder(...)
            Producer.beginTransaction()
                TransactionManager.beginTransaction()
                    TransactionManager.transitionTo()
    Producer.send(ProducerRecord<K, V> record, Callback callback)
        waitOnMetadata(...)
        keySerializer.serialize(...)
        valueSerializer.serialize(...)
        partition(...)
        RecordAccumulator.append(...)
```
```java
KafkaMessageListenerContainer$ListenerConsumer
    pollAndInvoke
        invokeInTransaction
            TransactionTemplate.execute
                doInvokeRecordListener
                    invokeOnMessage
```
