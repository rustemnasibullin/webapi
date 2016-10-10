package ru.mtt.webapi.core;

/**
 * MultyOperational component interface for unuification invocation of composite WebApi methods
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IOperationMap {
    
       String[] getActionList();
       String[] getOperationList(String actId);
       String[] getParametersList(String actId);
    
    
}
