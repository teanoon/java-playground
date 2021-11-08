# 持久化机制
Redis 是内存数据库，宕机后数据会丢失。

为了重启后快速恢复数据，需要使用持久化技术。

## Redis Database
redis 默认的存储方式，是一种二进制压缩文件，通过快照 Snapshot 实现。

### 触发快照的方式
* 符合自定义配置的快照规则
  * redis.conf 中配置 `save xxx yyy` 则可以表示在 xxx 分钟内至少 yyy 个键值发生变化时进行快照
* 执行 save 或 bgsave 或 flushall 命令
* 执行主从复制操作

### bgsave 的流程
1. Redis 父进程判断是否在执行 save/bgsave/bgrewriteaof 。
   1. 如果正在执行，则立即返回，不需要重复执行
2. fork 方式创建子进程（期间阻塞父进程，无法执行命令）
3. 在子进程中执行 bgsave 并提示 “Background saving started”（期间不再阻塞父进程）
4. 子进程创建或更新 RDB 文件
5. 子进程通知父进程，bgsave结束。

### RDB 文件结构
* REDIS: 固定字符串
* RDB_VERSION: RDB 版本号
* AUX_FIELD_KEY_VALUE_PAIRS:
* DB_NUM
* DB_DICT_SIZE
* EXPIRE_DICT_SIZE
* KEY_VALUE_PAIRS
* EOF
* CHECK_SUM

### pros & cons
pros:
* 文件体积小
* 便于传输
* 适合数据量较小的情况

cons:
* 不保证数据完整性，会丢失最后一次快照以后的修改

## Append-Only File
可选的存储方式，通过 `appendonly yes` 打开。

Append-only file 中保存的是 redis 的命令

### 执行过程
1. 命令传播：执行完的命令和参数信息会被发送到 AOF 中
2. 缓存追加：AOF 接收到新命令后，追加到 AOF 缓存中
3. 写入：
   1. 操作：
      1. WRITE: 根据条件，将 aof_buf 中的缓存写入到 AOF 文件
      2. SAVE: 根据条件，将 AOF 文件保存至磁盘
   2. SAVE 模式
      1. aof_fsync_no 不保存
      2. aof_fsync_everysec 每秒保存一次（默认）
      3. aof_fsync_always 每个命令都保存（不推荐）

aof_fsync_no 时， save 只会在下面其中一种情况下执行且会阻塞主进程：
* Redis 被关闭
* AOF 被禁用
* aof_buf 已满或者被刷新

aof_fsync_everysec
* 每秒钟保存一次
* 使用 fork 操作，不阻塞主进程
* 一般情况下丢失少于 2 秒的命令

aof_fsync_always
* 每次命令都保存
* 由主进程执行，阻塞主进程
* 最多只丢失一个命令

### 重写

### pros & cons
pros:
* 数据更完整

cons:
* 数据文件更大

## 混合
