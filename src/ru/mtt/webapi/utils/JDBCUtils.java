package ru.mtt.webapi.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import ru.mtt.webapi.core.WAPIException;

/**
 *  JDBC utility 
 *
 *  @author rnasibullin@mtt.ru
 */

public class JDBCUtils {
    
    
    DataSource dss = null;
    
    public JDBCUtils() {
        super();
    }


    public JDBCUtils(DataSource ds) {
        super();
        dss = ds;
    }

    public int execute (String cmd) {
        
           int xStatus = 0; 
          
        Statement stmt = null;
        Connection conn =  null;
        
        try {

            conn = dss.getConnection();
            stmt = conn.createStatement();
            stmt.execute(cmd);

         } catch(Exception e) {

            e.printStackTrace();
            xStatus = 1;
            
         } finally {
            //finally block used to close resources
            try {
                
                if (stmt!=null) stmt.close();
            
            } catch (SQLException se2){
            
            try {
                 if (conn!=null) conn.close();
            } catch(SQLException se) {
               se.printStackTrace();
            }

            }
         }
          
         return xStatus;
        
    }



    public static String getString(ResultSet resultSet, String fld) throws WAPIException {
     
        String d = null;
        
        try {
         
                d = resultSet.getString(fld);
            
        } catch (SQLException ee) {
              
        }
        
        return d;

    }


    public static Date getDate(ResultSet resultSet, String fld) throws WAPIException {
     
        Date d = null;
        
        try {
         
                d = resultSet.getDate(fld);
            
        } catch (SQLException ee) {
              
        }
        
        return d;

    }

    public static Long getLong(ResultSet resultSet, String fld) throws WAPIException {
     
        Long d = null;
        
        try {
         
                d = resultSet.getLong(fld);
            
        } catch (SQLException ee) {
            
        }
        
        return d;

    }

    public static Integer getInteger(ResultSet resultSet, String fld) throws WAPIException {
     
        Integer d = null;
        
        try {
         
                d = resultSet.getInt(fld);
            
        } catch (SQLException ee) {
            
        }
        
        return d;

    }

}
