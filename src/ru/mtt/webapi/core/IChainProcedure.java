package ru.mtt.webapi.core;


/**
 * Chained operation element interface 
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IChainProcedure extends IConstants  {
    
    
        Object execute(String procAlias, String[] prameters, Object evIN);
    
    
}
