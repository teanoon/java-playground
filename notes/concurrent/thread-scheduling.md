# Thread Scheduling
A framework of when to execute a thread and when to block a thread.

## 1. Native
1. Object#wait

    在条件满足前一直等待。

2. Object#notify

    唤醒同个对象监视器中等待的线程，配合 `Object#wait` 使用；如果有多个等待线程，按照某种规律选择其一。

    通过执行 `synchronize` ，线程将同步对象作为监视器使用。

3. Object#notifyAll

    与 `Object#notify` 类似，唤醒同个对象监视器中所有正在等待的线程。

1. Thread#interrupt

    中断抛出 `InterruptedException` 的命令，并执行线程 `Runnable` 剩余逻辑。
    | method             | interruptible | detail |
    | ------------------ | ------------- | ------ |
    | `Thread.sleep(x)`  | true          | 抛出 `InterruptedException` |
    | `Thread#join()`    | true          | 抛出 `InterruptedException` |
    | `Thread#join(x)`   | true          | 抛出 `InterruptedException` |
    | `wait()`           | true          | 抛出 `InterruptedException` |
    | `wait(x)`          | true          | 抛出 `InterruptedException` |
    | `LockSupport#park` | true          | 不抛异常，通过 `Thread#isInterrupted` 判断中断方法 |
    | `synchronized`     | false         | 阻塞，并不执行线程，直到获取锁 |

1. `synchronize`
    Native keyword

## 2. Lock & Condition 显性锁约定？
`Lock` 替代 `synchronize` ，通常使用 `ReentrantLock` ，另有一种 `ReadWriteLock` 约定了共享锁和独占锁的两种应用，提高读取时的效率。

`Condition` 替代 `wait` ， `notify` ， `notifyAll` ， 从而允许同一个对象拥有多组 Object monitor 。

## 3. Synchronization Barrier
1. Phaser
1. Semaphore
1. CountDownLatch
1. CyclicBarrier

## 4. ThreadPool
1. Executor

## 5. Advanced
1. Future/CompletableFuture
1. java.util.concurrent.atomic.*

## 6. Patterns:
1. ForkJoinPool
1. Immutable Pattern
1. Single threaded executor Pattern
1. Guarded suspension Pattern
1. Balking Pattern
1. Product-consumer Pattern
1. Read-write lock Pattern
1. Thread-per-message Pattern
1. Worker thread Pattern

## 7. Applications:
1. BlockingQueue
1. ConcurrentHashMap

## 8. Cases without Lock:
1. CAS
1. `volatile`
