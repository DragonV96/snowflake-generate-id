# snowflake-generate-id
java实现Twitter开源的轻量级、高并发、高可用的分布式id【snowflake算法】

# 背景

​		在复杂分布式系统中，往往需要对大量的数据和消息进行唯一标识。此时一个能够生成全局唯一ID的系统是非常必要的。业务系统对ID号的要求：

- 全局唯一
- 有序单调递增
- 高可用
- 低延迟

## 1. UUID

​		UUID(Universally Unique Identifier)的标准型式包含32个16进制数字，以连字号分为五段，形式为8-4-4-4-12的36个字符。

**优点：**

- 高性能
- 无网络消耗

**缺点：**

- 不易于存储：UUID太长，16字节128位，通常以36长度的字符串表示，很多场景不适用
- 信息不安全：基于MAC地址生成UUID的算法可能会造成MAC地址泄露，这个漏洞曾被用于寻找梅丽莎病毒的制作者位置
- D作为主键时在特定的环境会存在一些问题，比如做DB主键的场景下，UUID就非常不适用
	- MySQL官方有明确的建议主键要**尽量越短越好**，36个字符长度的UUID不符合要求
	- 对MySQL索引不利：如果作为数据库主键，在InnoDB引擎下，UUID的无序性可能会引起数据位置频繁变动，**严重影响性能**

## 2. 数据库生成ID

​		以MySQL举例，利用给字段设置`auto_increment_increment`和`auto_increment_offset`来保证ID自增，每次业务使用下列SQL读写MySQL得到ID号。

**优点：**

- 非常简单，利用现有数据库系统的功能实现，成本小，有DBA专业维护。
- ID号单调自增，可以实现一些对ID有特殊要求的业务。

**缺点：**

- 强依赖DB，当DB异常时整个系统不可用，属于致命问题。配置主从复制可以尽可能的增加可用性，但是数据一致性在特殊情况下难以保证。主从切换时的不一致可能会导致重复发号。
- ID发号性能瓶颈限制在单台MySQL的读写性能。

# 雪花算法id

## 1. id结构

​		生成的id结构如下：

````
 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 |			|				   |		  |
1bit符号位		41bit时间戳			10bit机器id	12bit序列号
````

- 1`bit`：第一位符号位，固定为0，表示正数
- 41`bit`：41位表示毫秒级时间戳**【可以表示（1L<<41）/(1000L * 3600 * 24 * 365)=69年的时间】**
- 10`bit`：10位机器id可以分别表示1024台机器**【如果对IDC划分有需求，还可以将10-bit分5-bit给IDC，分5-bit给工作机器。这样就可以表示32个IDC，每个IDC下可以有32台机器，可以根据自身需求定义】**
- 12`bit`：最后12位表示序列号**【每台机器每毫秒内最多生成2^12即4096个序列号】**

​		理论上snowflake方案的QPS约为409.6w/s，这种分配方式可以保证在任何一个IDC的任何一台机器在任意毫秒内生成的ID都是不同的。

## 2. 优点

- 有序单调递增，且全局唯一

- 高性能，低延迟

- 不依赖第三方系统，以服务的方式部署，稳定性高

- 可根据自身业务特性分配bit位，非常灵活

	官方给出数据为每台机器至少每秒生成10k条数据，且响应时间在2毫秒以内。

## 3. 缺点

​		强依赖于机器的时间，一旦时间回滚则无法保证id唯一性。

## 4. 适用场景

​		多用于分布式微服务场景中（跨机房，集群）。

# 本机实测

​		**测试实例：**

​		**1000万条数据测试三次，分别耗时约68.3秒、79.0秒、76.3秒，平均消耗约74.5秒**

​		**大约每秒生成134.2k数据（id）**

​		操作系统： WIN 7 Service Pack 1 （64位）

​		测试工具：IntelliJ IDEA 2019.1.1（Ultimate Edition）

​		系统配置： 

				- 处理器：Xeon E3-1231 v3 @ 3.40GHz 四核
				- 内存：16GB
				- 硬盘：固态256GB

​		输出结果1：

````
	...
c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861858
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861859
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861860
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861861
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861862
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861863
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861864
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861865
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589093426543861866
2019-10-31 23:12:26.355  INFO 14084 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : It takes 68261 milliseconds totally!
````

​		输出结果2：

````
	...
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527388
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527389
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527390
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527391
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527392
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527393
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527394
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527395
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589101181392527396
2019-10-31 23:43:15.255  INFO 12136 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : It takes 79068 milliseconds totally!
````

​		输出结果3：

````
	...
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497434
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497435
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497436
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497437
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497438
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497439
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497440
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497441
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497442
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497443
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : SnowFlake generate id = 6589102540292497444
2019-10-31 23:48:39.242  INFO 8664 --- [           main] c.g.snowflake.SnowflakeApplicationTests  : It takes 76338 milliseconds totally!
````

