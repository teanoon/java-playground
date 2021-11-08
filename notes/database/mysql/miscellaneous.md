# show processlist
查看用户正在运行的线程信息，root用户能查看所有线程，其他用户只能看自己的：
* id：线程ID，可以使用kill xx；
* user：启动这个线程的用户
* Host：发送请求的客户端的IP和端口号
* db：当前命令在哪个库执行
* Command：该线程正在执行的操作命令
  * Create DB：正在创建库操作
  * Drop DB：正在删除库操作
  * Execute：正在执行一个PreparedStatement
  * Close Stmt：正在关闭一个PreparedStatement
  * Query：正在执行一个语句
  * Sleep：正在等待客户端发送语句
  * Quit：正在退出
  * Shutdown：正在关闭服务器
* Time：表示该线程处于当前状态的时间，单位是秒
* State：线程状态
  * Updating：正在搜索匹配记录，进行修改
  * Sleeping：正在等待客户端发送新请求
  * Starting：正在执行请求处理
  * Checking table：正在检查数据表
  * Closing table : 正在将表中数据刷新到磁盘中
  * Locked：被其他查询锁住了记录
  * Sending Data：正在处理Select查询，同时将结果发送给客户端
* Info：一般记录线程执行的语句，默认显示前100个字符。想查看完整的使用show full processlist;

# InnoDB vs MyISAM
|    | InnoDB | MyISAM |
| -- | ------ | ------ |
| 事务和外键 | 支持，数据具有安全性和完整性 | 不支持，读写较快？ |
| 锁机制    | 行级锁，基于索引实现        | 表级锁 |
| 索引结构   | 聚簇索引，索引和记录一起存储 | 非聚簇索引，索引和记录分开 |
| 并发能力   | MVCC 支持高并发           | 写并发低 |
| 存储文件   | .frm .idb .ibdata1 ，最大 64 TB | .frm .myd .myi ，最大 256 TB |

more to see: https://dev.mysql.com/doc/refman/8.0/en/storage-engines.html
