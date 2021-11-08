# 概念
* transaction: 事务
  * 独立的操作，不会夹杂执行被事务外的命令
  * 原子操作，要么全部执行，要么全部不执行
    * 但中途出错则跳过剩余命令，这个特例可在开发阶段规避，不会出现在生产环境中
  * 所有命令按序执行
  * Redis 使用 append-only file 保证单次即可将事务写入磁盘。
  * 当 Redis 意外关机：
    * 如果事务未完全提交（未执行 EXEC ），那么整个事务会被抛弃
    * 如果 append-only file 不完整或单次写入不完整，那么重启后 Redis 会检测到不完整的事务。
  * 不支持回滚
    * 导致事务失败的情况可在开发阶段避免，生产环境中不应出现。类似的问题也无法通过回滚事务拯救。
    * 性能较好
* multi: 开启事务，后续命令放入 CommandQueue
* exec: 提交事务，执行 CommandQueue 中所有的命令
* discard: 清空 CommandQueue 中的命令
* watch: 在开启事务前 watch ，那么当事务提交前 watch 的对象发生变化时，清空事务队列
  * CAS 操作
  * EXEC 提交后，结束所有 key 的 watch 状态
  * watch 的 client 断开连接后，也会结束 watch 状态
  * 6.0.9 之前，过期的 key 不会取消事务

# 事务执行过程
1. Redis 底层：
   1. 通过 flags=REDIS_MULTI 表达操作正在事务中
   2. 通过 commands 存储事务中的命令
2. 执行 MULTI 时， flags=REDIS_MULTI ，后续命令存入 commands
   1. 如果遇到语法错误， flags=REDIS_DIRTY_EXEC 并清空 commands
3. 执行 EXEC 时， 提交并执行 commands
   1. 如果遇到类型操作错误，那么会取消剩余命令

# Watch 执行过程
1. Redis 底层：
   1. 通过 watched_keys 保存被监视的 key 和监视它的客户端列表
2. 执行 MULTI 时， flags=REDIS_MULTI ，后续命令存入 commands
3. 如果被监视的 key 发生变化， flags=REDIS_DIRTY_CAS 并情况 commands
