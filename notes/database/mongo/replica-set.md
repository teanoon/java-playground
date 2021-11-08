# Components
## Primary member
## Secondary members
## Arbiter

# 自动故障转移 Automation Failover

# 读操作 Read Operations
## Read Reference
默认从 Primary 读取，可通过指定 read reference 从 secondaries 读取。

多文档事务则必须从 Primary 读取。

# oplog entry
```json
{
    "ts": Timestamp + counter,
    "h": global id,
    "v": version,
    "op": operation type(insert/update/delete),
    "ns": namespace,
    "o": object
}
```

# replication
初始化同步：全量同步
    * 第一次同步
    * 落后数据大于 oplog 大小
keep 复制同步：基于 oplog 增量同步

# Voting
## Primary voting
* 初始化复制集
* Secondary 权重高于 Primary ，发生替换选举
* 丢失 Primary 时
* Primary 不能访问到大部分成员时

## Voting process
二阶段 + 多数派协议（过半投票）
