# 提供IO相关的工具类

[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.rnkrsoft.io4j/io4j/badge.svg)](http://search.maven.org/#search|ga|1|g%3A%22com.rnkrsoft.io4j%22%20AND%20a%3A%22io4j%22)

```xml
<dependency>
     <groupId>com.rnkrsoft.io4j</groupId>
        <artifactId>io4j</artifactId>
        <version>最新版本号</version>
</dependency>
```

1. 字节缓冲区，移植Netty中的ByteBuf
2. 动态文件，基于文件系统的事务化操作，支持多线程下写入多个版本，最终只有一个最新的文件版本，并且支持回滚，提交。


