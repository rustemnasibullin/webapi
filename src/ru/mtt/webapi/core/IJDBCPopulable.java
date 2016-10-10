package ru.mtt.webapi.core;

import java.sql.ResultSet;

/**
 * Inteface for Identification object as Populable from JDBC resultset 
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IJDBCPopulable {
    
    
       void populate (ResultSet resultSet) throws WAPIException;
    
    
}
