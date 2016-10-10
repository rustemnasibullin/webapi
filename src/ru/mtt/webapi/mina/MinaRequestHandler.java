package ru.mtt.webapi.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.HttpRequest;

/**
 * Created by R.Nasibullin on 8/14/14
 * HTTP Server Core service interface
 * 
 *  @author rnasibullin@mtt.ru
 */
public interface  MinaRequestHandler {


       public HTTPResponseWrapper acceptRequest(IoSession sess, String msgDecoded);
       public HTTPResponseWrapper acceptRequest(IoSession sess, HttpRequest msg);


}
