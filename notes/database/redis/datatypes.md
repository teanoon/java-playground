数据类型和数据结构

# 1. 数据类型
key 是字符串

value 有：
* string
  * 整数
  * 浮点数
* list
* set
* sorted set
* hash
* stream
* bitmap
* geo

## 1.1. Geo
geohash 算法将二维坐标转换成字符串
1. Z阶曲线将多维信息转换为一维二进制数据
2. Base32 编码将二进制数据编写成可见的字符串，字符串集合来自于 0-9/b-z(去掉 a i l o)

底层使用 zset 存储，使用 geohash 编码后的字符串作为 value ，使用 zset 的 score 排序就可以得到坐标附近的其他元素。

# 2. 部分操作
* SETEX => SET key value; EXPIRE key xxx-seconds
* SETNX => set key value NX
  * NX: if **N**ot e**X**ists
  * atomic
* LPUSHX: LPUSH only when the key exists
* RPUSHX
* RPOPLPUSH: RPOP and LPUSH it to another list
  * atomic
* BRPOP: RPOP or block until one is available.

# 3. 数据结构
Redis 一个实例默认分配 16 个 RedisDB

## 3.1. RedisDB
每个 RedisDB 包含 key-value 数据。
```c++
// redis db
typedef struct redisDb {
  int id;
  long avg_ttl;
  // 存储所有 key-value
  dict *dict;
  dict *expires;
  dict *blocking_keys;
  dict *ready_keys;
  dict *watched_keys;
}
```

## 3.2. RedisDict
Redis Dict 是 Redis 的核心模块，它的实现为
```c++
// 字典
typedef struct Dict {
  DictHt ht[2];
  DictType *type;
}
```
```c++
// 字典 Hash 表
typedef struct DictHt {
  DictEntry *table;
  unsigned long size;
  unsigned long sizeMask;
  unsigned long sizeUsed;
}
```
```c++
// 字典 Hash 表 Entry
typedef struct DictEntry {
  void *key;
  union{
    void *val;
    uint64_t u64;
    int64_t s64;
    double d;
  } value;
  // key hash 冲突时指向下一个 entry
  struct DictEntry *next;
}
```

**存储和读取**

存储时会根据 key 计算 Hash 值，保存在字典里。取值时可用指针方式直接获取。

**Hash 冲突**

如果不同的 key 得到了相同的 Hash 值，那么字典会在相同位置保存多份 key 和 value 。取值时会取到多个 key 和 value 的组合，再根据 key 过滤，得到正确的 value 。

**字典扩容 Rehash**
当字典 Hash 表不够用的时候：
1. 初次申请默认容量为 4 个 DictEntry
2. 非初次申请则申请当前 Hash 表容量的两倍
3. 申请的内存通过 h[1] 使用
4. 新增数据都在新表 h[1] 中创建
5. 旧数据在 h[0] 中依然提供修改、删除、查询的功能
6. 将 h[0] 表重新计算索引后，全部迁移到 h[1] 中

* 对于主要存储内容，为什么不一次申请全部内存空间？

## 3.3. RedisObject
key 是 String ， value 是 RedisObject
```c++
typedef struct redisObject {
  // string, set, list, hash ...
  unsigned type:4;
  // string type 的可以是 int encoding
  unsigned encoding:4;
  void *ptr;
  int refCount;
  unsigned lru:LRU_BITS;
  // ...
}
```

## 3.4. Redis types and implementations
RedisObject 有多种 type ，对应的实现分别是：
* Simple Dynamic Strings Header
  * type=string 的实现
* skip list (B-Tree ?)
  * sorted set
* zip list
  * sorted set （数量较小时）
* int set
  * set （元素都是整数且在 2^64 有符号整数范围内）
* quick list
  * list
  * publish/subscribe
  * 结合了 ad list 和 zip list 优势
  * 可对内容进行压缩
* list pack + Rax （有序字典树）
  * Stream

## Encoding
不同 Type 可以用不同 Encoding 来提升效率、节省内存空间

String 可以有：
* int 整数类型
* raw 大字符串
* embstr 小字符串，适用于长度小于 44 个字节的字符串

List 可以有：
* quick list

Hash 可以有：
* Dict
* Zip list 元素较少，且是小整数或短字符串

zset 可以用：
* skip list + dict
* zip list
