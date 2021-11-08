- [1. 角色](#1-角色)
- [2. Acknowledgement](#2-acknowledgement)
  - [2.1. Producer Acknowledgement](#21-producer-acknowledgement)
  - [2.2. Consumer offset commit](#22-consumer-offset-commit)
- [3. Kafka Rebalance Protocol](#3-kafka-rebalance-protocol)
  - [3.1. Cause](#31-cause)
  - [3.2. Process](#32-process)
  - [3.3. Cost](#33-cost)
  - [Incremental Cooperative Rebalancing](#incremental-cooperative-rebalancing)
- [4. Election](#4-election)
  - [4.1. ISR vs 过半选举](#41-isr-vs-过半选举)
- [5. Persistence](#5-persistence)
- [6. Reliability](#6-reliability)
  - [6.1. At most once](#61-at-most-once)
  - [6.2. At least once](#62-at-least-once)
  - [6.3. Exactly once (Transactional)](#63-exactly-once-transactional)

# 1. 角色
* Zookeeper 协调
* Event
* Broker (Leader)
* Topic
* Partition (Leader)
* Replica
* Producer
* Consumer (Group/Leader)

# 2. Acknowledgement
## 2.1. Producer Acknowledgement
* ack=0
* ack=1
* ack=-1/all

## 2.2. Consumer offset commit
* auto commit
* manual sync commit
* manual async commit

```java
try {
    while(true) {
        ConsumerRecords<String, String> records =
        consumer.poll(Duration.ofSeconds(1));
        process(records); // 处理消息
        // 使⽤异步提交规避阻塞
        // 异步提交不重试，如果失败了，下次异步提交可重新同步 offset
        // 如果还有剩余 offset 未能通过异步提交同步，则由 finally 中的同步提交完成同步（可重试）
        commitAysnc();
    }
} catch(Exception e) {
    handle(e); // 处理异常
} finally {
    try {
        consumer.commitSync(); // 最后⼀次提交使⽤同步阻塞式提交
    } finally {
        consumer.close();
    }
}
```

# 3. Kafka Rebalance Protocol
## 3.1. Cause

## 3.2. Process
1. JoinGroup
   1. Consumer 发送 JoinGroup 请求
   2. Broker coordinator 在收到所有 Consumer 请求后，或超时后响应
   3. Consumer Group Leader 收到包含活跃成员列表的响应
   4. 其余 Consumer 成员收到空响应
   5. Consumer Group Leader 在本地进行分区分配
2. SyncGroup
   1. Consumer Group Leader 发送分配计划
   2. 其余 Consumer 成员发送空请求
   3. Broker Coordinator 在收到所有 Consumer 请求后响应
   4. 所有 Consumer 成员收到分配计划
3. 期间发生错误则重新开始？

## 3.3. Cost
* Stop-the-world
* 降低 Rebalance 频率，牺牲部分 Partition 稳定性

## Incremental Cooperative Rebalancing
由 2.3/2.4 引入的新方式，采用了内嵌式的协议，取代了全局 Rebalancing ，由多个局部 Rebalance 阶段完成。

# 4. Election
## 4.1. ISR vs 过半选举
ISR >> OSR

# 5. Persistence

# 6. Reliability
## 6.1. At most once
## 6.2. At least once
## 6.3. Exactly once (Transactional)
