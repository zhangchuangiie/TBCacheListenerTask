package com.example.demo.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    //工程内默认路径，configNameLocal只需要传文件名
    public static Object getYmlConfigLocal(String configNameLocal,Object key) {
        Resource resource = new ClassPathResource(configNameLocal);
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties = yamlFactory.getObject();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return properties.get(key);
    }

    //工程内默认路径，configNameLocal只需要传文件名
    public static Object getPropConfigLocal(String configNameLocal,Object key) {
        Properties properties = null;
        try {
            InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(configNameLocal);
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return properties.get(key);
    }
    //外部任意路径，configName是相对或绝对全路径
    public static Object getYmlConfig(String configName,Object key) {
        //Resource resource = new ClassPathResource(configName);
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("file:"+configName);
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties = yamlFactory.getObject();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return properties.get(key);
    }
    //外部任意路径，configName是相对或绝对全路径
    public static Object getPropConfig(String configName,Object key) {
        Properties properties = null;
        try {
            //InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(configName);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(configName));

            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return properties.get(key);
    }
    //用于本地测试和打包发布两种模式的适配，不需要更改代码路径
    public static Object getYmlConfigAuto(String configName,String configNameLocal,Object key) {
        Object o = getYmlConfig(configName,key);
        if(o == null){
            o = getYmlConfigLocal(configNameLocal,key);
        }
        return o;
    }
    //用于本地测试和打包发布两种模式的适配，不需要更改代码路径
    public static Object getPropConfigAuto(String configName,String configNameLocal,Object key) {
        Object o = getPropConfig(configName,key);
        if(o == null){
            o = getPropConfigLocal(configNameLocal,key);
        }
        return o;
    }

}
