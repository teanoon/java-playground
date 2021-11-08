# Isolation
## read-uncommitted
当前事务可看到其他事务提交前的修改。

会出现脏读、不可重复读和幻读。

## read-committed
当前事务只能在其他事务提交后看到那些提交的内容。

会出现可不重复读和幻读。

## snapshot(default)
只能读到当前事务开启前的版本（ snapshot ）。

会出现幻读。

## Serializable
只使用一个线程执行事务。
