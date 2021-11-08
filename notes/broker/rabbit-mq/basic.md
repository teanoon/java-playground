- [1. 角色](#1-角色)
- [2. Persistence](#2-persistence)
- [3. Process](#3-process)
  - [3.1. Producer send](#31-producer-send)
  - [3.2. Consumer receive](#32-consumer-receive)
- [4. Use case](#4-use-case)
  - [4.1. Work Queue](#41-work-queue)
  - [4.2. Fanout / PubSub](#42-fanout--pubsub)
  - [4.3. Topic](#43-topic)
  - [4.4. Quality of Service](#44-quality-of-service)
  - [4.5. Deal Letter Exchange 死信队列](#45-deal-letter-exchange-死信队列)
  - [4.6. Delayed Message Exchange](#46-delayed-message-exchange)
  - [4.7. RPC](#47-rpc)
- [5. Message Acknowledge](#5-message-acknowledge)
  - [5.1. Producer confirm](#51-producer-confirm)
  - [5.2. Consumer confirm](#52-consumer-confirm)
- [6. Throttle 限流](#6-throttle-限流)
  - [6.1. Producer throttle 生产者限流](#61-producer-throttle-生产者限流)
- [7. Reliability 可靠性](#7-reliability-可靠性)
  - [7.1. At least once 最少一次](#71-at-least-once-最少一次)
  - [7.2. At most once 最多一次](#72-at-most-once-最多一次)
  - [7.3. Exact once 刚好一次](#73-exact-once-刚好一次)
  - [7.4. 可靠性验证](#74-可靠性验证)
- [8. Q](#8-q)
  - [8.1. 为什么要设置 Exchange 和 Queue 两个模块而不是合并为一个模块？](#81-为什么要设置-exchange-和-queue-两个模块而不是合并为一个模块)
  - [8.2. 当生产者发送消息时，先有 Exchange 接收，但若：](#82-当生产者发送消息时先有-exchange-接收但若)
  - [8.3. Temporary 队列 (x-expires/exclusive) 的使用场景](#83-temporary-队列-x-expiresexclusive-的使用场景)
  - [8.4. Message relay](#84-message-relay)

# 1. 角色
* Producer
* Consumer
* Broker
* Exchange
* Binding
* Queue

# 2. Persistence
消息有：
* 持久化消息
* 非持久化消息

存储层包含：
* 队列索引：index file/store file
  * /var/lib/.../<node>/msg_stores/vhosts/<host>/queues/<queue>/<x>.idx
  * /var/lib/.../<node>/msg_stores/vhosts/<host>/queues/<queue>/journal.jif
* 消息存储：
  * msg_store_persistent: /var/lib/.../<node>/msg_stores/vhosts/<host>/queues/msg_store_persistent/<x>.rdq
  * msg_store_transient: 内存？

当消息小于 `queue_index_embed_msgs_below` 时，队列索引也会保存消息。

读取消息时：
1. 1
2. 2
3. 3
4. 4

删除消息时：
1. 1
2. 2
3. merge segment
4. 4

# 3. Process
## 3.1. Producer send
1. 建立 TCP connection，开启 Channel
2. 声明 Exchange （类型、是否持久化）
3. 声明 Queue （是否排他、是否持久化、是否自动删除）
4. 通过 routingKey 绑定 Exchange 和 Queue
5. 发送消息，其中包含 routingKey 和 Exchange
6. Exchange 根据 routingKey 查找 Queue
7. Exchange 向 Queue 发送消息
8. 如果未找到消息，则根据生产者提供的配置选择处理：
  1. 退回生产者
  2. 抛弃

## 3.2. Consumer receive
1. 建立 TCP connection，开启 Channel
2. 请求消费特定队列的消息
3. 等待特定队列中的消息
4. 接收消息
5. 确认接收到的消息
6. Broker 删除已经被确认的消息

# 4. Use case
## 4.1. Work Queue
多个消费者消费同一个 Queue ，实现负载均衡效果。

## 4.2. Fanout / PubSub
广播模式，忽略 routingKey

## 4.3. Topic
Topic 可以通过通配符匹配（ *# ）多个 Queue

## 4.4. Quality of Service
通过观察 Prefetch 可以了解到消费者的负载，如果 Prefetch 满了，则代表当前消费者负载很高，那不应继续向其推送新的消息。

仅对推模式有效，拉模式可以由消费者自行控制。

## 4.5. Deal Letter Exchange 死信队列
以下情况导致消息变成死信：
* 消息被拒绝，且不重新入队
* 消息过期
* 队列达到最大长度

可用死信队列进行回调处理未能成功处理的消息

## 4.6. Delayed Message Exchange
Exchange 保留消息一定时间后再发送给 Queue

可以通过 rabbitmq_delayed_message_exchange 实现

## 4.7. RPC
![rpc](rabbit-rpc-pattern.png)
1. Client 创建临时 Callback Queue(exclusive=true)
2. Client 向 Server 定义好的 Queue 发送请求，并带上 reply_to=callback_queue 和 correlation_id
3. Server 处理请求，并将结果发送给 callback_queue
4. Client 回调 callback_queue ，比较 correlation_id 是否匹配，接受匹配的返回，抛弃不匹配的返回

# 5. Message Acknowledge
## 5.1. Producer confirm
确保消息正确发送，并持久化。

有三种实现方式：
| implementation     | pros | cons |
| ------------------ | ---- | ---- |
| synchronous        | easy to implement, like rpc | poor performance |
| batch, synchronous | easy to implement, like rpc; better performance | bad granularity |
| asynchronous       | best performance and granularity | more involved to implement |

## 5.2. Consumer confirm
确保消息正确消费

# 6. Throttle 限流
## 6.1. Producer throttle 生产者限流
磁盘或者内存用量达到阈值后，阻塞生产者发送消息。

disk limit

memory limit

# 7. Reliability 可靠性
## 7.1. At least once 最少一次
* 开启事务机制或者 Publisher Confirm 机制，生产者可确保将消息发送到 Broker
* 消息和队列持久化
* 消费端通过业务确认消息，而不是自动确认
* 消息幂等性保证消息不会重复处理
  * 通过构造数据库唯一索引拒绝重复操作
  * 使用乐观锁拒绝重复、过期操作
  * 使用唯一 ID 做排他锁，这个唯一 ID 可以通过 Header 附加在消息上

## 7.2. At most once 最多一次
最不可靠

## 7.3. Exact once 刚好一次
尚未实现，属于强一致性，需要牺牲性能

## 7.4. 可靠性验证
通过开启 rabbitmq_tracing 插件可收集特定队列的信息

# 8. Q
## 8.1. 为什么要设置 Exchange 和 Queue 两个模块而不是合并为一个模块？

Exchange 是一个路由的角色：
* 会根据 routingKey 把消息转发到指定 Queue
  * 一个 Exchange 可以有多个 routingKey
  * 一个 routingKey 可以绑定多个 Queue
  * 一个 Queue 可以绑定多个 routingKey
  * 一个 Queue 可以绑定多个 Exchange
* 会根据发送类型选择 Direct/Fanout/Topic/Headers 等发送模式

## 8.2. 当生产者发送消息时，先有 Exchange 接收，但若：
* 此时没有合适的 Queue ，会如何处理？
* 后续新增 Queue ，旧数据如何处理？

## 8.3. Temporary 队列 (x-expires/exclusive) 的使用场景


## 8.4. Message relay
