- [1. Record Locks(row-level)](#1-record-locksrow-level)
  - [1.1. Shared Locks](#11-shared-locks)
  - [1.2. Exclusive Locks](#12-exclusive-locks)
- [2. Gap Locks](#2-gap-locks)
- [3. Next-key Locks](#3-next-key-locks)
- [4. 意向锁（ table-level ）](#4-意向锁-table-level-)
  - [4.1. Intension shared lock(IS)](#41-intension-shared-lockis)
  - [4.2. Intension exclusive lock(IX)](#42-intension-exclusive-lockix)
  - [4.3. Insert Intension Locks](#43-insert-intension-locks)
- [5. Auto-inc Locks(table-level)](#5-auto-inc-lockstable-level)
- [6. Predicate Locks](#6-predicate-locks)
- [7. 手动加锁](#7-手动加锁)

这里主要讨论 InnoDB 的锁实现

# 1. Record Locks(row-level)
针对索引记录（ B+Tree 叶子节点）的锁，如果该表没有手动创建索引，那么就会自动创建聚簇索引。

具体来说， `SELECT c1 FROM t WHERE c1 = 10 FOR UPDATE;` 为 `c1 = 10` 的索引记录添加了锁（如果不是唯一索引，则可能对应多个数据库记录）。

## 1.1. Shared Locks
```sql
SELECT ... LOCK IN SHARE MODE
```

## 1.2. Exclusive Locks
这些操作若没用到索引，就会锁住全表？
```sql
SELECT ... FOR UPDATE
UPDATE
DELETE
INSERT ？
```

# 2. Gap Locks
针对索引记录（ B+Tree 叶子节点 ）前后空白区域的锁。

具体来说， `SELECT c1 FROM t WHERE c1 BETWEEN 10 and 20 FOR UPDATE;` 为 `10 < c1 < 20` 的索引区域添加了锁，例如 `c1 = 15` 这样的值就无法创建、更新或删除。

这个锁涵盖的索引区域，可能是：
1. 单个索引值
2. 多个索引值
3. 空区域

# 3. Next-key Locks
是 Record Locks 和 Gap Locks 的综合体，但比 Gap Locks 涵盖更广范围？
```
(negative infinity, 10]
(10, 11]
(11, 13]
(13, 20]
(20, positive infinity)
```

# 4. 意向锁（ table-level ）
意向锁不阻塞任何操作，除了全表请求，如 `LOCK TABLES ... WRITE` 。

意向锁的主要目的是提示这张表有一个行锁或即将产生一个行锁。

如果有多个事务同时访问某张表？：
1. 当事务 A 通过 `SELECT ... LOCK IN SHARE MODE` 访问记录集合 A
2. 该表获取 IS-A 和 S-A 两个锁
3. 当事务 B 通过 `UPDATE` 访问记录集合 B
4. 该表获取 IX-B ，并提示即将获取 X 锁，可能是针对集合 B 的 Gap Locks
5. 如果集合 A 和 集合 B 的锁不冲突，则该表可以正式获取 X-B 锁
6. 当事务 C 通过 `UPDATE` 访问记录集合 C
7. 该表获取 IX-C ，并提示即将获取 X 锁，可能是针对集合 C 的 Gap Locks
8. 当集合 B 和 集合 C 的锁冲突，则该表无法获取 X-C 锁，事务 C 会等待 X-B 锁的释放
9. 此时该表有 IS-A 、 IX-B 、 IX-C 、 S-A 和 X-B 5个锁， X-C 正在队列中

对于 IS 和 S ：
1. 在获取 shared lock 之前，事务必须先获取 IS 或者更强的锁
2. 获取 IS 后，则代表已经存在 S 或者 即将获得 S 锁
3. 获取 S 后， 则代表必然获取 IS ？

对于 IX 和 X ：
1. 在获取 exclusive lock 之前，事务必须获取 IX
2. 获取 IX 后，则代表已经存在 X 或者 即将获得 X 锁，那么就无法再加 X 锁
3. 获取 X 后，则代表必然获取 IX ，那么就无法再通过获取 IX 来追加新的 X 锁？

| 新锁\已有 | X      | IX       | S      | IS |
| -------- | ------ | -------- | ------ | -- |
| X        | 无法获取 | 无法获取 | 无法获取 | 无法获取？ |
| IX       | 无法获取？ | 可获取   | 无法获取？ | 可获取 |
| S        | 无法获取 | 无法获取？ | 可获取   | 可获取 |
| IS       | 无法获取？ | 可获取   | 可获取   | 可获取 |

## 4.1. Intension shared lock(IS)
eg, `SELECT ... LOCK IN SHARE MODE`

## 4.2. Intension exclusive lock(IX)
eg, `SELECT ... FOR UPDATE`

## 4.3. Insert Intension Locks
是一种 Gap Locks ，提示多个事务将要执行的插入动作不会插入到同一个新位置，这些事务可以并行处理。

# 5. Auto-inc Locks(table-level)
当插入带有自增键的表时，事务会获取这一种特殊的表级排他锁。

# 6. Predicate Locks
在 Repeatable Read 或 Serializable 隔离级别下使用 Spatial 索引时，之前的 Next-key Locks 并不适用。

这里就需要 Predicate Locks ，工作原理类似 Gap Locks ，对查询条件下的范围加排他锁，无法读写？

# 7. 手动加锁
`LOCK TABLE XXX READ|WRITE;` 可为表添加读/写锁。
