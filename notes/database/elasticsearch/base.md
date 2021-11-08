# Node types
* Master-eligible node
  * node.master = true
  * node.data = true/false
  * Being able to be elected as a master
* Data node
  * node.master = true/false
  * node.data = true
  * Stores shards
* Dedicated master node
  * node.master = true
  * node.data = false
  * Stores no shards
  * Being able to be elected as a master
  * quorum-based decision to prevent split-brain
* Dedicated client node
  * node.master = true
  * node.data = false
  * Stores no shards
  * coordinating-only: load balancer/relay queries to certain nodes
  * ingest-only: pre-processing documents in ingesting pipelines

# 数据完整性保证
1. 创建新文档，添加至
   1. In-memory buffer
   2. 基于文件的 translog ，写入和请求默认是同步的
2. refresh 创建新的 segment ，并使其搜索可见
3. flush 将内存中的 segments 写入磁盘并清空 translog

# BM25

# DocValues

# Bool Filter

# search_after
