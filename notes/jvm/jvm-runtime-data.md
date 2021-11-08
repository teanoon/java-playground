# JVM runtime data structure
# Thread-specific
## 1. VM stack
create a list of stack frames to execute Java methods.

Each stack frame has:
1. local variable table
1. etc

## 2. Native method stack
load implemented native methods.

> HotSpot combines Native method stack and VM stack into one.

## 3. Program counter register
instruct the execution position of VM stack after CPU phase shifted

# Thread-shared
## 4. Method area
1. Type information
1. Runtime constant pool
1. Statick varibles
1. JIT Compiled caches
1. etc

## 5. Heap
store almost all Java objects and arrays.

## 6. Meta-space

# Direct memory
Not a part of runtime data.

JDK 1.4 NIO uses this area
