# TBCacheListenerTask
一个基于MySQL封装的类Zookeeper通知风格的工具

## 使用步骤（以监听业务库表user为例）：
1. 创建对应的监听表user_listener，见user_listener.sql
2. 为监听业务库表user创建触发器，见user.sql
3. 集成TBCacheListenerTask类，内置初始化同步，定时刷新同步，当监听业务库表user出现增删改变化时，自动同步并触发回调函数，三个回调函数可以自定义，见TBCacheListenerTask.java
4. 其他业务类需要访问监听业务库表user的内存镜像时，提供了8个外部调用接口，见TBCacheListenerTask.java

## 适用场景：
1.需要Zookeeper通知机制，但是又是轻量工程只有数据库一种外部组件
2.需要频繁访问业务库表在内存中的镜像，但是要求内存镜像与数据库表准实时同步

## 联系人：
有问题可以联系:zhangchuang@iie.ac.cn
