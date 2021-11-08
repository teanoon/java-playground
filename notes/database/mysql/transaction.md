- [1. ACID](#1-acid)
  - [1.1. Write-Ahead Logging](#11-write-ahead-logging)
  - [1.2. Atomicity](#12-atomicity)
  - [1.3. Consistency](#13-consistency)
  - [1.4. Isolation](#14-isolation)
  - [1.5. Durability](#15-durability)
  - [1.6. BASE](#16-base)
- [2. 事务控制](#2-事务控制)
  - [2.1. 并发事务](#21-并发事务)
  - [2.2. InnoDB Multi-Versioning(MVCC)](#22-innodb-multi-versioningmvcc)
  - [2.3. 隔离级别](#23-隔离级别)
    - [2.3.1. Repeatable Read](#231-repeatable-read)
    - [2.3.2. Read committed](#232-read-committed)
    - [2.3.3. Read uncommitted](#233-read-uncommitted)
    - [2.3.4. Serializable](#234-serializable)
    - [2.3.5. 与并发问题的关系](#235-与并发问题的关系)
- [3. note](#3-note)
- [4. Q](#4-q)

# 1. ACID
## 1.1. Write-Ahead Logging
先写日志，后写磁盘，是 Atomicity/Isolation/Duration 实现的基础。

## 1.2. Atomicity
写操作修改 BufferPool ，形成脏页数据，并生成 Redo 和 Undo 日志。

数据库崩溃时，会有以下情况：
| 写入日志 | 脏页写入磁盘 | 操作                       |
| -------- | ------------ | -------------------------- |
| 成功     | 全部成功     | 满足原子性，无需操作       |
| 成功     | 部分成功     | Undo ，撤销写入操作        |
| 成功     | 部分失败     | ？                         |
| 成功     | 全部失败     | Redo ，重新执行写入操作    |
| 失败     | 失败         | 没有任何数据写入，无需操作 |

最后根据 Commit 提交的记录判断执行 Undo 或 Redo 。

## 1.3. Consistency
约束一致性：
1. 外键
2. 唯一索引
3. Check ？

数据一致性，由 AID 保障

## 1.4. Isolation
隔离性从低到高分别为：
1. Read uncommitted 读未提交
2. Read committed 读提交
3. Repeatable read 可重复读
4. 可串行化

通过锁和多版本控制 MVCC 实现。

## 1.5. Durability
完整提交触发保障性操作：
1. Binlog 落地
2. Binlog 发送（主从同步）
3. 存储引擎提交
   1. flush_logs ：写入日志
   2. check_point ：脏页刷盘操作等
   3. 事务提交标记：完成写操作后

## 1.6. BASE
* **B**asically **A**vailable
* **S**oft state
* **E**ventually consistent

# 2. 事务控制
## 2.1. 并发事务
现象
* 更新丢失：多个事务更新同一行记录时，互相覆盖导致数据丢失。
  * 回滚覆盖：一个事务回滚，覆盖了其他事务已提交的修改
  * 提交覆盖：一个事务提交，覆盖了其他事务已提交的修改
* 脏读：一个事务读取到了另一个事务修改但未提交的数据。
* 不可重复读：一个事务多次读取同一行记录，取得不同的数据。
* 幻读：一个事务多次查询，取得不同数量的结果。
* 多版本读：一个事务既有查询，也有修改时，可能查询到新旧数据混杂的结果？

解决方案
* 队列
* 排他锁
* 读写锁：读读并行，读写、写读和写写都采用排他锁。
* MVCC ：读读、读写、写读并行。写写采用排他锁

## 2.2. InnoDB Multi-Versioning(MVCC)
定义：每次更新都会保存变动记录的旧版本信息，以供：
1. 并发读取
2. 回滚操作

数据细节：
1. InnoDB 会在 undo tablespaces 的 rollback segment 保存变更记录的旧版本信息
2. 这些信息统称为 undo log record ，存在 insert 和 update 两种 undo logs
3. 这些信息除了当前记录的数据外，还包括：
   * DB_ROW_ID
   * DB_TRX_ID
   * DB_ROLL_PTR
4. 这些信息用来执行 undo 操作或者保障一致性读

清除逻辑：
1. insert undo logs 在当前事务结束后清除
2. INSERT 事务执行失败时，使用该记录回滚
3. update undo logs 在没有事务访问 snapshot 时清除，而 snapshot 就是基于 update undo logs 构造
4. `DELETE` 是一个特殊的 `UPDATE` ，当对应的 update undo log 清除后才会物理删除
5. 物理删除的操作称为 purge
6. 反复批量插入和删除时容易造成 purge 线程堆积

隔离级别：
1. Repeatable Read 和 Read Committed 采用了一致性非阻塞读
2. Read Uncommitted 采用非阻塞读，但不保证一致性，即并不在读取时创建 snapshot ，不会访问 update undo logs
3. Serializable 在 autocommit = 0 时采用 `LOCK IN SHARE MODE` 读取，阻塞其他更新，或等待锁的释放。
4. Serializable 在 autocommit = 1 时读取本身就是一个事务。

## 2.3. 隔离级别
指事务在读写过程中操作锁的策略。
### 2.3.1. Repeatable Read
可重复读，保证每次读取的内容一致。
1. 默认隔离级别
2. Non-blocking reads:
   1. 由事务中的第一个读创建副本
   2. 后续读均读这个副本
3. `SELECT FOR UPDATE` / `SELECT LOCK IN SHARE MODE` / `UPDATE` / `DELETE`
   1. 唯一索引搜索只锁该条唯一记录，不影响 B+Tree 上前方的位置
   2. 其他索引搜索则通过 gap locks 或 next-key locks 对索引扫描范围内的所有结果和所有空位加锁
   3. 所有结果阻塞 `UPDATE` / `DELETE`
   4. 所有空位阻塞 `INSERT`
   5. 不涉及索引的搜索会？
   6. 会更新 Non-blocking reads 的 snapshot ？

### 2.3.2. Read committed
只读已提交的内容，可以读取到其他提交的内容，即不可重复读。
1. 当前读创建副本，并由当前读独有
2. `SELECT FOR UPDATE` / `SELECT LOCK IN SHARE MODE` / `UPDATE` / `DELETE`
   1. 涉及索引的搜索锁定索引记录，但不锁定索引空位
   2. 不涉及索引的搜索会？
   3. 允许插入新记录
   4. gap locks 仅在外键检查和唯一值检查时使用？
   5. 会产生幻读
   6. 仅支持 row-based binary logging
3. `UPDATE` / `DELETE`
   1. 涉及索引的搜索锁定会在 where 执行完后释放
   2. 只保留对索引记录的锁
   3. 这个过程可减少死锁的情况
4. `UPDATE`
   1. 会读取一次索引中的记录的最新提交版本（ undo log snapshot ）
   2. 会等待这些记录的锁，并当解锁后重新读一次再加锁
5. 比 `innodb_locks_unsafe_for_binlog` 更灵活，可在任意 Session 或全局下任意时间激活

### 2.3.3. Read uncommitted
可以读到其他事务未提交的内容。

在 Read committed 基础上， `SELECT` 始终按 Non-blocking 操作，可能出现脏读：
1. 某个写事务开启
2. 该事务读取某行记录并加锁
3. 该事务开始更新
4. 该事务在 undo log 中创建本次更新的记录
5. `SELECT` 开始（基于 undo log ？）创建并读取 snapshot ，此时为脏读？
6. 该事务将修改写入磁盘
7. 该事务完成提交

### 2.3.4. Serializable
在 Repeatable read 基础上：
1. 如果 autocommit 禁用，所有 `SELECT` 始终按 `SELECT ... LOCK IN SHARE MODE` 操作，阻塞其他事务的更新或等待其他事务释放锁
2. 如果 autocommit 激活，则 `SELECT` 会有自己的事务，

### 2.3.5. 与并发问题的关系
| 隔离级别          | 回滚覆盖 | 提交覆盖 | 脏读 | 不可重复读 | 幻读 |
| ---------------- | -------- | -------- | ---- | ---------- | ---- |
| Read Uncommitted | x        | y        | y    | y          | y    |
| Read Committed   | x        | ?        | x    | y          | y    |
| Repeatable Read  | x        | x        | x    | x          | ?    |
| Serializable     | x        | x        | x    | x          | x    |

# 3. note
* 空位： https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_gap
* `SELECT ... LOCK IN SHARE MODE`：
  * 优先占用时，其他事务可读但不可写，需要等待锁的释放
  * 读取记录已有锁时，等待锁的释放后读取

# 4. Q
* 不涉及索引的搜索会使用全表的聚簇索引？
* 一致性非阻塞读：
  * 当前事务中任意读取总能保证获得同样的结果或当前事务提交的最新修改
  * 同一个事务中，即使 `SELECT` 已经创建 snapshot ，也能通过 `UPDATE` / `DELETE` 来更新 snapshot ？
    ```sql
    SELECT COUNT(c2) FROM t1 WHERE c2 = 'abc';
    -- Returns 0: no rows match.
    UPDATE t1 SET c2 = 'cba' WHERE c2 = 'abc';
    -- Affects 10 rows: another txn just committed 10 rows with 'abc' values.
    SELECT COUNT(c2) FROM t1 WHERE c2 = 'cba';
    -- Returns 10: this txn can now see the rows it just updated.
    ```
  * 读版本由读事务提交时间点决定
    ```sql
    SET GLOBAL autocommit=0;
    -- Session A -> empty set
    SELECT * FROM t;

    -- Session B
    INSERT INTO t VALUES (1, 2);

    -- Session A -> empty set
    SELECT * FROM t;

    -- Session B
    COMMIT;

    -- Session A -> empty set
    SELECT * FROM t;

    -- Session A
    COMMIT;

    -- Session A -> 1 record
    SELECT * FROM t;
    ```
