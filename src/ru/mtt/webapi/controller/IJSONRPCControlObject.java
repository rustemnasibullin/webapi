package ru.mtt.webapi.controller;


/**
 *  JSON-RPC Control interface  
 * 
 *  @author @author RNasibullin@mtt.ru
 */
public interface IJSONRPCControlObject {
    
    String getMethod();
    String getId();
    String getJsonrpc();
    Object getParamsList();
    Object getResult();

    
}
