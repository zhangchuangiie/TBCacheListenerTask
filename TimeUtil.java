package com.example.demo.util;

import com.example.demo.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Component
public class TimeUtil {

    //加载顺序：Constructor >> @Autowired >> @PostConstruct
//    @Autowired
//    private BaseMapper baseMapper;
//
//    private static TimeUtil timeUtil;
//
//    @PostConstruct
//    public void init() {
//        timeUtil = this;
//        timeUtil.baseMapper = this.baseMapper;
//    }



    public static String timeStamp2DateString(Timestamp timeStamp) {


        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fm.format(timeStamp);


    }






    /**
     * 将long类型的时间戳转换成yyyy-MM-dd HH:mm:ss格式的字符串
     * @param timeStamp long类型
     * @return yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public static String timeStamp2DateString(long timeStamp) {

        String str = timeStamp2DateString(timeStamp, "yyyy-MM-dd HH:mm:ss");

        return str;

    }

    /**
     * 将long类型的时间戳转换成pattern格式的字符串
     * @param timeStamp long 类型的时间戳
     * @param pattern 代表转换的格式的字符串
     * @return pattern格式的字符串
     */
    public static String timeStamp2DateString(long timeStamp, String pattern) {

        if (timeStamp > 0) {
            SimpleDateFormat fm = new SimpleDateFormat(pattern);
            return fm.format(timeStamp);
        } else {
            System.out.println("时间格式有误。。。");
            return "";
        }

    }

    /**
     * 以某种格式获得当前时间
     * @param pattern 格式
     * @return
     */
    public static String getCurrentDateString(String pattern) {
        long stamp = System.currentTimeMillis();

        return timeStamp2DateString(stamp, pattern);
    }


    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getCurrentDateString() {

        //timeUtil.baseMapper.count("SELECT count(*) FROM user");
        return getCurrentDateString("yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getCurrentDayString() {

        //timeUtil.baseMapper.count("SELECT count(*) FROM user");
        return getCurrentDateString("yyyy-MM-dd");
    }

    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getYesterdayString() {

        long stamp = System.currentTimeMillis()-1000*60*60*24;
        return timeStamp2DateString(stamp, "yyyy-MM-dd");
    }

    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getBefore24HoursString() {

        long stamp = System.currentTimeMillis()-1000*60*60*24;
        return timeStamp2DateString(stamp, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getCurrentDateStringMillisecond() {
        return getCurrentDateString("yyyy-MM-dd HH:mm:ss.SSS");
    }
    /**
     * 按默认时间格式获得当前时间
     * @return
     */
    public static String getCurrentDateStringYearAndMonth() {
        return getCurrentDateString("yyyy")+"/"+getCurrentDateString("MM");

    }


    /**
     * (1)能匹配的年月日类型有：
     *    2014 年4 月19 日
     *    2014年4月19日
     *    2014年4月19号
     *    2014-4-19
     *    2014/4/19
     *    2014.4.19
     *    19.4.2014
     *    19-4-2014
     *    19/4/2014
     * (2)能匹配的时分秒类型有：
     *    15:28:21
     *    15:28
     *    5:28 pm
     *    15点28分21秒
     *    15点28分
     *    15点
     * (3)能匹配的年月日时分秒类型有：
     *    (1)和(2)的任意组合，二者中间可有任意多个空格
     *
     * 注意： 如果dateStr中有多个时间串存在，只会匹配第一个串，其他的串忽略
     *
     * @param text
     * @return (1)和(2)的任意组合格式的字符串
     */
    public static String matchDateString(String dateStr) {
        try {
            List matches = null;
            Pattern p = Pattern.compile("(\\d{1,4}(\\s)*[-|\\/|年|\\.](\\s)*\\d{1,2}(\\s)*[-|\\/|月|\\.](\\s)*\\d{1,4}(\\s)*([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
            Matcher matcher = p.matcher(dateStr);
            if (matcher.find() && matcher.groupCount() >= 1) {
                matches = new ArrayList();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String temp = matcher.group(i);
                    matches.add(temp);
                }
            } else {
                matches = Collections.EMPTY_LIST;
            }
            if (matches.size() > 0) {
                dateStr = ((String) matches.get(0)).trim();
            } else {

                dateStr = "";
            }
        } catch (Exception e) {
            return "";
        }

        return dateStr;
    }


}
