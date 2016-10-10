package ru.mtt.webapi.mina;

import org.apache.mina.core.session.IoSession;

/**
 * 
 * MINA Request Processing interface
 *
 *
 * @author rnasibullin@mtt.ru
 */
public interface IMinaHttpProcessor {
  
    
       IoSession getSession();
       void setMessageEncoded (String msg);
    
    
}
