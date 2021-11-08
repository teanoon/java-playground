# 索引类型
* Single Field Index 单键索引，
  * 支持 ttl ，过期自动删除文档
* Compound Index 复合索引（按照查询最左匹配）
* Multi Key Index 多键索引（数组）
* Text Index 文本索引
* Wildcard Index 匹配符索引（查询内嵌文档） 4.2+
* 2D Sphere Index / 2D Index
* Hashed Index Hash 索引（适合等值查询）

# 索引结构
B-Tree by WireTiger since 3.2
> WiredTiger maintains a table's data in memory using a data structure called a B-Tree ( B+ Tree to be specific), referring to the nodes of a B-Tree as pages. Internal pages carry only keys. The leaf pages store both keys and values.
> http://source.wiredtiger.com/10.0.0/tune_page_size_and_comp.html

* _id 是默认唯一索引，随 Collection 时创建
* 全部 Index 的数据最好不超过内存数量

# 存储引擎 WireTiger
3.2 及之后的默认引擎，同时可选的还有 InMemory

## Document-Level concurrency
* 大多数情况下只使用意向锁阻塞冲突
* 冲突的修改会自动重试
* 在 `collMod` 之类的操作时会用到 Collection-Level exclusive locks

## MVCC
* 操作初始阶段读取数据并创建 Snapshot
* 写入时创建 Checkpoint 并和上一个 Checkpoint 保持一致
* 写入中断后可从上一个 Checkpoint 开始使用 Journal 恢复
* 意外退出后，可从上一个 Checkpoint 开始恢复
* 新的 Checkpoint 创建后，metadata table 指向这个新的 Checkpoint 并释放上一个 Checkpoint 的 page？？？

## Journal
是一种 Write Ahead Log 。这种系统与 Checkpoints 一起保证数据持久性。

### 日志过程
1. 发生写操作时，使用写操作的内容创建 Journal Record
2. Journal Record 会在内存 Buffer 中保存
3. 当以下情况发生时，写入磁盘：
   1. For replica set members: 有等待 oplog 的操作
   2. For replica secondary members: 每次 oplog 批量应用后
   3. 写操作包含或提示 Write Concern `j: true`
   4. 每隔 `storage.journal.commitIntervalMs`(100ms)
   5. Journal 文件超过 100MB 时，将当前文件的全部 Record 写入磁盘并创建新的 Journal 文件、删除旧文件

### 恢复过程
1. 在数据文件中查找上一个有效 Checkpoint
2. 在日志文件中查找上一个有效 Checkpoint 对应的记录
3. 执行上一个有效 Checkpoint 后的所有操作

## Compression
用 CPU 换取数据存储的压力

block-compression: 集合压缩

prefix-compression: 索引压缩，相同的索引前缀只保存一次

## Memory
通常，默认缓存大小 = Max(250MB, 0.5 * (RAM - 1GB))

对于 Container 环境，使用最大内存数。

# Explain
## support
```js
// 3.+
aggregate()
count()
find()
remove()
update()
// 3.2+
distinct()
findAndModify()
// 4.4+
mapReduce()
```

## Output queryPlanner
### namespace
<database>.<collection>

### indexFilterSet
表明是否使用了索引过滤

### queryHash
Query Shape 的 hash ，用于识别慢查询。

Query Shape 由查询条件、排序和映射组成，不包含查询条件的值。

### planCacheKey
???

### optimizedPipeline
???

### winningPlan
```json
"winningPlan" : {
   "stage" : <STAGE1>,
   ...
   "inputStage" : {
      "stage" : <STAGE2>,
      ...
      "inputStage" : {
         "stage" : <STAGE3>,
         ...
      }
   }
}
```
最优计划，以 Stages 树的形式呈现。一个 Stage 可以有一个或多个 InputStage 。

* stage
  * COLLSCAN: 扫描集合
  * IXSCAN: 扫描索引键
  * FETCH: 获取文档
  * SHARD_MERGE: 合并多个分片的结果
  * SHARDING_FILTER: ？？？
* inputSage(s): 显示内嵌文档或索引键及其 Stage

## rejectedPlans
其他候选 Plan 列表

## Output executionStats
详细描述了查询计划的执行细节，
* 在 verbosity = executionStats 时，返回最优计划的细节
* 在 verbosity = allPlansExecution 时，返回所有计划的细节
### nReturned
查询匹配的文档总数

### executionTimeMillis
查询计划的总消耗时间

### totalKeysExamined
扫描的索引值的数量，通常在 IXSCAN 阶段发生？

### totalDocsExamined
扫描的文档数次（扫描多个文档，某些文档可能多次扫描），通常在 COLLSCAN 和 FETCH 阶段发生

### executionStages
详细描述了计划的阶段细节，每个阶段都包含这些信息：
* executionTimeMillisEstimate
* works - work units 的数量，每次查询会分配多个任务，可以分配的任务类型有：
  * 扫描单键索引
  * 获取单个文档
  * 映射文档
  * doing a piece of internal bookkeeping???
* advanced: 阶段性返回的中间过程结果的数量
* needTime: 阶段性任务的 work cycle 数量
* needYield: 由存储层发起请求，但查询阶段中止处理并释放锁的次数？？？
* saveState: 查询阶段中止处理并**准备**释放锁的次数
* restoreState: 查询阶段恢复上次执行阶段的次数，如重新获取已释放的锁
* isEOF: 执行阶段是否到达 Stream 尾部。

### inputStage.keysExamined
### inputStage.docsExamined
### inputStage.seeks

## Output serverInfo
* Host
* Port
* version
* gitVersion
