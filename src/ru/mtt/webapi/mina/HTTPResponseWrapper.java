package ru.mtt.webapi.mina;

/**
 * Created by R.nasibullin on 8/20/14.
 * Typical HTTP Response wrapper
 *
 *
 * @author rnasibullin@mtt.ru
 */
public class HTTPResponseWrapper {

       String content;
       String contentType;
       String charSet;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public HTTPResponseWrapper(String content, String charSet, String contentType) {
        this.content = content;
        this.charSet = charSet;
        this.contentType = contentType;
    }
}
