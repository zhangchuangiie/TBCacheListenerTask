package com.example.demo.trigger.schedule;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.mapper.BaseMapper;
import com.example.demo.util.TimeUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Order(10) //指定顺序
public class UserTBCacheListenerTask implements CommandLineRunner {

    @Resource
    private BaseMapper baseMapper;


    private String monitorTable = "`user`";
    private String monitorKey = "id";
    private String ListenerTable = "`user_listener`";

    private Map<Object, JSONObject> myMap = new ConcurrentHashMap<Object,JSONObject>();
    //hashmap是线程不安全的，而hashtable性能低下，所以concurrentHashMap应运而生。

    //获得实时集合(外部调用1)
    public Map<Object, JSONObject> getMyMap() {
        return myMap;
    }

    //获得实时集合迭代器，线程安全的(外部调用2)
    public Iterator<Map.Entry<Object, JSONObject>> getIterator(){
        Iterator<Map.Entry<Object, JSONObject>> iterator = myMap.entrySet().iterator();
        return iterator;
    }

    //获得当前集合快照，不是绝对线程安全的(外部调用3)
    public Map<Object, JSONObject> getSnapshot(){
        Map<Object, JSONObject> myMapSnapshot = new ConcurrentHashMap<Object,JSONObject>();

        long start = System.currentTimeMillis();   //获取开始时间
        myMapSnapshot.putAll(myMap);

        long end = System.currentTimeMillis(); //获取结束时间
        System.out.println("@@@@@@@@@@程序运行时间： " + (end - start) + "ms");
        return myMapSnapshot;
    }

    //获得当前集合快照(List版本)，不是绝对线程安全的(外部调用4)
    public List<JSONObject> getSnapshotList(){
        Map<Object, JSONObject> myMapSnapshot = new ConcurrentHashMap<Object,JSONObject>();

        long start = System.currentTimeMillis();   //获取开始时间
        myMapSnapshot.putAll(myMap);
        List<JSONObject> myList = new ArrayList<JSONObject>(myMapSnapshot.values());
        ///可以根据需求进行排序
        myList.sort(Comparator.comparing(obj -> ((JSONObject) obj).getObject(monitorKey,Long.TYPE)).reversed());
        long end = System.currentTimeMillis(); //获取结束时间
        System.out.println("@@@@@@@@@@程序运行时间： " + (end - start) + "ms");
        return myList;
    }

    //containsKey(外部调用5)
    public boolean containsKey(Object id) {
        return myMap.containsKey(id);
    }

    //getValue(外部调用6)
    public JSONObject getValue(Object id) {
        return myMap.get(id);
    }

    //size(只对当前状态负责)(外部调用7)
    public int size() {
        return myMap.size();
    }

    //getKeys(只对当前状态负责)(外部调用8)
    public Object[] getKeys() {
        return myMap.keySet().toArray();
    }

    //put(假写接口1)
    public JSONObject put(Object key, JSONObject value) {
        return myMap.put(key,value);
    }

    //remove(假写接口2)
    public JSONObject remove(Object key) {
        return myMap.remove(key);
    }


    //如果有业务逻辑可以在这里添加
    private void addCallback(Object data_id){
    }
    //如果有业务逻辑可以在这里添加
    private void uptCallback(Object data_id){
    }
    //如果有业务逻辑可以在这里添加
    private void delCallback(Object data_id){
    }


    //private int initFlag = 0;
    private long id = 0;   ///含义是currentMaxIdHadSynchronized
    //private String startTimeStr = TimeUtil.getCurrentDateString();


    private String initSql = "SELECT * FROM "+monitorTable;    //可以根据情况加条件筛选
    private Map<Object,JSONObject> initHandler(){
        Map<Object, JSONObject> myMap = new ConcurrentHashMap<Object,JSONObject>();
        List<LinkedHashMap<String, Object>> result = baseMapper.select(initSql);
        for (LinkedHashMap<String, Object> o : result) {
            Object key_id = o.get(monitorKey);   //monitorKey可以不是id
            myMap.put(key_id,JSONObject.parseObject(JSON.toJSONString(o)));
        }
        return myMap;
    }

    private String monitorSql = "SELECT * FROM "+ListenerTable+" where id > ?";     ///时钟问题，这个条件先不加and time > ?";
    private List<LinkedHashMap<String, Object>> monitorHandler(){
        return baseMapper.select(monitorSql, id);
    }

    private String addSql = "SELECT * FROM "+monitorTable+" where id = ?";
    private void addHandler(Object data_id){
        LinkedHashMap<String, Object> o = baseMapper.get(addSql, data_id);
        System.out.println("新增数据 = " + JSON.toJSONString(o));
        myMap.put(data_id,JSONObject.parseObject(JSON.toJSONString(o)));
        System.out.println("当前数据量："+ myMap.size());
        System.out.println("当前数据："+ JSON.toJSONString(myMap));
        addCallback(data_id);
    }

    private String uptSql = "SELECT * FROM "+monitorTable+" where id = ?";
    private void uptHandler(Object data_id){
        LinkedHashMap<String, Object> o = baseMapper.get(uptSql, data_id);
        System.out.println("修改数据 = " + JSON.toJSONString(o));
        myMap.put(data_id,JSONObject.parseObject(JSON.toJSONString(o)));
        System.out.println("当前数据量："+ myMap.size());
        System.out.println("当前数据："+ JSON.toJSONString(myMap));
        uptCallback(data_id);
    }


    private void delHandler(Object data_id){
        System.out.println("删除数据 = " + data_id);
        myMap.remove(data_id);
        System.out.println("当前数据量："+ myMap.size());
        System.out.println("当前数据："+ JSON.toJSONString(myMap));
        delCallback(data_id);
    }


    //添加定时任务
    @Scheduled(cron = "0/60 * * * * ?")     //可以根据需要改短，一秒都没问题，因为监听记录表几乎都是空的
    //@Scheduled(cron = "0 5 0 * * ?")每天00:05:00执行
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void monitorTasks() {

        long start=System.currentTimeMillis();   //获取开始时间
        List<LinkedHashMap<String, Object>> result =  monitorHandler();
        System.out.println("监听表名：user");
        System.out.println("监听刷新时间："+ TimeUtil.getCurrentDateString());
        System.out.println("监听版本号："+ id);
        if(result.size() == 0){
            System.out.println("监听的库表没有变化");
        }else{
            System.out.println("监听的库表发生变化");
            System.out.println(JSON.toJSONString(result));
            for (LinkedHashMap<String, Object> o : result) {
                String type = (String) o.get("type");
                Object data_id = o.get("data_id");
                System.out.println(type+":"+data_id);
                ////TODO
                switch (type)//值必须是整型或者字符型
                {
                    case "add":
                        addHandler(data_id);
                        break;
                    case "upt":
                        uptHandler(data_id);
                        break;
                    case "del":
                        delHandler(data_id);
                        break;
                }
            }
            id = (long) result.get(result.size()-1).get("id");
        }
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

    }

    private String clearSql = "delete FROM "+ListenerTable+" where time < ?";
    //添加定时任务(清理过期修改动作)
    @Scheduled(cron = "0 5 0 * * ?") //每天00:05:00执行
    private void clearTasks() {

        long start=System.currentTimeMillis();   //获取开始时间
        String clearTimeStr = TimeUtil.getBefore24HoursString();
        int result = baseMapper.delete(clearSql,clearTimeStr);

        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

    }


    //添加定时任务(每日凌晨做一次纠正动作)
    @Scheduled(cron = "0 15 0 * * ?") //每天00:15:00执行
    private void refreshTasks() {

        long start=System.currentTimeMillis();   //获取开始时间
        myMap = initHandler();
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

    }

    private void initMaxId() {

        long start=System.currentTimeMillis();   //获取开始时间
        //先取当前最大监听记录ID后初始加载原始业务表，理论上可能会重复刷新最新变化，但是不会丢失变化，重复刷新一般问题不大
        String maxIdSql = "SELECT IFNULL(MAX(id),0) as max_id FROM "+ListenerTable;
        Long maxId = baseMapper.count(maxIdSql);
        id = maxId;
        System.out.println("maxId = " + maxId);
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

    }


    //添加加载启动
    @Override
    public void run(String... args) throws Exception {

        initMaxId();
        myMap = initHandler();
        System.out.println("初始化加载数据 = " + myMap.size());

    }



}
