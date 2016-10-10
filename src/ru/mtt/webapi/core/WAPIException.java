package ru.mtt.webapi.core;


/**
 * Typical unified WebApi Exception 
 * 
 * @author rnasibullin@mtt.ru
 */

public class WAPIException extends Throwable {
    public WAPIException(String string, Throwable throwable, boolean b, boolean b1) {
        super(string, throwable, b, b1);
    }

    public WAPIException(Throwable throwable) {
        super(throwable);
    }

    public WAPIException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public WAPIException(String string) {
        super(string);
    }

    public WAPIException() {
        super();
    }
    
    
}
