# Redis 实现
```java
// get lock
jedis.set(keyId, requestId, "NX", "PX", expiringTime);
```
```java
// release lock

```
