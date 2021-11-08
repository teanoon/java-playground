Advanced Message Queueing Protocol

# Concepts
* Publisher
* Consumer
* Server/Broker
* Virtual host
* Exchange
* Routing key
* Bindings
* message Queue

# 传输层架构
采用二进制协议

基础单位是数据帧：
* 帧头
* 负载
* 帧尾

一次完整的信息由一个或多个数据帧组成。

多个信息可共用一个 socket 连接。

一次 socket 连接会有多个 Channel 。

每一次信息会根据数据帧按序分配个一个给定 Channel 。

## 数据帧数据类型
* int
* bits(flags)
* short string
* long string
* field tables

## 协议协商

## 数据帧界定
