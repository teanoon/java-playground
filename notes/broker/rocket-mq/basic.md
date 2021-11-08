- [1. 角色](#1-角色)
- [2. Producer send](#2-producer-send)
- [3. Consumer consume](#3-consumer-consume)
  - [3.1. 消费方式](#31-消费方式)
  - [3.2. Flow control via sentinel](#32-flow-control-via-sentinel)
  - [3.3. Parallelism](#33-parallelism)
  - [3.4. Filter](#34-filter)
  - [3.5. 优化 Consumer 消费](#35-优化-consumer-消费)
- [4. Storage](#4-storage)
  - [4.1. 组成](#41-组成)
- [5. 零拷贝](#5-零拷贝)
- [6. HA 高可用](#6-ha-高可用)
  - [6.1. Replica](#61-replica)
  - [6.2. 消费者高可用](#62-消费者高可用)
  - [6.3. 生产者高可用](#63-生产者高可用)
- [7. Reliability](#7-reliability)
  - [7.1. At most once](#71-at-most-once)
  - [7.2. At least once](#72-at-least-once)
  - [7.3. Exactly once (Transactional)](#73-exactly-once-transactional)
- [8. 负载均衡](#8-负载均衡)
  - [8.1. 生产者负载均衡](#81-生产者负载均衡)
  - [8.2. 消费者负载均衡](#82-消费者负载均衡)
- [9. 死信队列](#9-死信队列)
- [10. 延迟消息](#10-延迟消息)
- [11. 顺序消息](#11-顺序消息)
- [12. 消息事务](#12-消息事务)
- [13. 消息查询](#13-消息查询)
- [14. 消息优先级](#14-消息优先级)

# 1. 角色
* Producer
* Consumer
* Broker
* NameServer
* Topic & tag
* Message Queue: 一个主题的多个分区

# 2. Producer send
* Group
* Producer Instance
* Sync/Async
* Retry
* Exception
* Duplicates
* One-way
* Parallelism
* 900k tps

# 3. Consumer consume
## 3.1. 消费方式
* Push
* pull
* PubSub
* Clustered
## 3.2. Flow control via sentinel
## 3.3. Parallelism
## 3.4. Filter
* Tag 过滤：通过比较 ConsumerQueue 的 tag hash code
* SQL92 过滤：可使用 SQL 表达式过滤，更灵活、支持复杂逻辑，使用 rocketmq-filter 解析执行、 BloomFilter 缓存
* Filter Server 过滤：在 Broker 上启动 Filter Server 进程。使用 Java 代码过滤，更灵活但消耗更多 CPU

## 3.5. 优化 Consumer 消费
1. 采用 Cluster 模式并增加 Cluster Consumer 数量，但不超过 Message Queue(Read Queue) 数量
2. 批量消费
3. 丢弃消息

# 4. Storage
## 4.1. 组成
* Commit Log 底层存储文件
* Consumer Queue 逻辑队列索引文件
* IndexFile 提供 key 或时间搜索的索引文件

# 5. 零拷贝

# 6. HA 高可用
* Master 提供读写
* Slave 提供读

## 6.1. Replica
* Sync
* Async
* Dledger
  * 写入时，消息被半数以上节点确认后，才算写入成功
  * 半同步半异步
  * Master 不可用时可选择产生新 Master
  * 保证切换前后消息严格顺序

## 6.2. 消费者高可用
Master 繁忙、不可用时，通过 Slave 消费

## 6.3. 生产者高可用
* 多主多写
* Dledger

# 7. Reliability
## 7.1. At most once
## 7.2. At least once
## 7.3. Exactly once (Transactional)

# 8. 负载均衡
## 8.1. 生产者负载均衡
生产者按罗宾轮询方式向同一主题的某一个 Message Queue(Write Queue) 发送消息

## 8.2. 消费者负载均衡
* 同一主题的不同 Message Queue(Read Queue) 在同一时间只被同一消费组内的一个消费者消费
* 多个 Message Queue 会平均地分配到不同的消费者进行消费
* 如果消费者数量大于 Message Queue ，部分消费者会闲置
* 加入新消费者时，所有消费者会一起重新分配（ doRebalance ） Message Queue

# 9. 死信队列
* 重试次数过多则进入死信队列

# 10. 延迟消息

# 11. 顺序消息
* 全局顺序：类似 DB Binlog 的场景需要全局顺序
* 局部顺序：按照特定 key 划分 Message Queue 即可保证对 key 的局部顺序（ Read vs Write ？）

# 12. 消息事务
通过两次提交的方式确保 Broker 与 DB 的一致性。

# 13. 消息查询
* 按照 Message Id 查询
* 按照 Message Key 查询

# 14. 消息优先级
* 分 Topic
* 分 Message Queue
* 消费者处理
