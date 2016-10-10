package ru.mtt.webapi.core;

import java.util.List;
import java.util.Map;

/**
 * BigData In Memory processinfg interface  
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IMemCache  {
    
    
    public <T> boolean insertOrReplace(T o);
    public <T> boolean insert(T o);
    public <T> boolean replace(T o);
    public <T> boolean remove(T o);
    public <T> boolean update(String keyAttribute, Object attributeValue, Map changeValues, Class<T> c);
    public <T> List<T> findByAttribute(String keyAttribute, Object attributeValue, Class<T> c);
    public <T> T findByKey(Object  keyAttributeValue, Class<T> c);
    public <T> List<T> readAllByClass (Class<T> c);
    public void setQueueMode (boolean flQueue);

    
}
