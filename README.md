# TBCacheListenerTask
一个基于MySQL封装的类Zookeeper通知风格的工具

## 适用场景：
1. 需要Zookeeper通知机制，但是又是轻量工程只有数据库一种外部组件
2. 需要频繁访问业务库表在内存中的镜像，但是要求内存镜像与数据库表准实时同步

## 使用步骤（以监听业务库表user为例）：
1. 创建user表对应的监听记录表user_listener，见user_listener.sql
2. 为监听业务库表user创建触发器，见user.sql
3. 集成UserTBCacheListenerTask类，一般只需要修改monitorTable（监听哪个表），monitorKey（哪个字段作为key）,ListenerTable（监听记录表），这三个配置，内置初始化同步，定时刷新同步，当监听业务库表user出现增删改变化时，自动同步并触发回调函数，三个回调函数可以自定义，见UserTBCacheListenerTask.java
4. 其他业务类需要访问监听业务库表user的内存镜像时，提供了8个外部调用接口，见UserTBCacheListenerTask.java
5. 另外，提供两个假写接口，方便本地即时生效，见UserTBCacheListenerTask.java
6. 如果被监听的表里面的key是字符串字段，也没问题，哪里都不需要改
7. 其中使用的BaseMapper类见https://github.com/zhangchuangiie/SimpleMybatis
8. 提供时间格式化工具类，见TimeUtil.java
9. user_listener与UserTBCacheListenerTask配对使用，每增加一个需要监听的业务表aaa，就增加一对aaa_listener与AaaTBCacheListenerTask


## 联系人：
有问题可以联系:zhangchuang@iie.ac.cn
