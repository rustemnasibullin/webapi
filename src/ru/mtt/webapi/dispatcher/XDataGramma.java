package ru.mtt.webapi.dispatcher;

import java.io.Serializable;

/**
 * Datagramma for trends gathering and identification interface
 * 
 * @author rnasibullin@mtt.ru
 */

public interface XDataGramma extends Serializable {
    
    

    abstract public Long getTs();
    abstract public Double getValue(String attr);


}
