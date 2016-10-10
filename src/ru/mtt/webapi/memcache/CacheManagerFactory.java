package ru.mtt.webapi.memcache;

import java.io.File;

import java.util.List;

import net.sf.ehcache.CacheManager;

import org.apache.commons.io.FileUtils;

import org.springframework.core.io.ClassPathResource;

import ru.mtt.webapi.utils.XUtils;

public class CacheManagerFactory {
    
    static org.springframework.cache.ehcache.EhCacheManagerFactoryBean instance = null;
    static Object monitor = new Object(); 
    
    
    public CacheManagerFactory() {
           super();
    }
    
    
    public org.springframework.cache.ehcache.EhCacheManagerFactoryBean getCManagerInstance ()  {
        
           synchronized (monitor) {
               String conf = "cfg/ehcache.xml";
               
           try {
                List<String> lst = FileUtils.readLines(new File ("./ehcache.conf"));
                conf = lst.get(0);    
           } catch (Throwable ee) {
                XUtils.ilog ("log/ehcache.log","Error: " + ee.getMessage());
           }
               
               
           if (instance == null) {
               instance = new org.springframework.cache.ehcache.EhCacheManagerFactoryBean(); 
               instance.setConfigLocation (new ClassPathResource(conf));
               instance.setShared (true);
           }
        
           }
           
           return instance;
        
    }
    
    
}
