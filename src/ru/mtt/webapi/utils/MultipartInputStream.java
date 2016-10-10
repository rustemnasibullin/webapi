package ru.mtt.webapi.utils;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.fileupload.MultipartStream;
/**
 *   MultyPartInput stream implementation 
 *
 *  @author rnasibullin@mtt.ru
 */

public class MultipartInputStream  {
    
    
    byte[] body  = null;
    byte[] boundary = null;
    
    
    public MultipartInputStream() {
        super();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBoundary(byte[] boundary) {
        this.boundary = boundary;
    }

    public byte[] getBoundary() {
        return boundary;
    }

    public MultipartInputStream(byte[] body, byte[] boundary) {
        super();
        this.body = body;
        this.boundary = boundary;
    }

    public byte[] decode () throws IOException {
        
           ByteArrayInputStream content = new ByteArrayInputStream(body);
           ByteArrayOutputStream out = new ByteArrayOutputStream (); 
           MultipartStream multipartStream =  new MultipartStream(content, boundary);
           boolean nextPart = multipartStream.skipPreamble();
           while (nextPart) {
                  String header = multipartStream.readHeaders();
                  multipartStream.readBodyData(out);
                  nextPart = multipartStream.readBoundary();
                  break;
           }
           return out.toByteArray();
        
    }
    
}
