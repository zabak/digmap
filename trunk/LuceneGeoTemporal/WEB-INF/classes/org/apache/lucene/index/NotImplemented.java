package org.apache.lucene.index;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 13:00:32
 * @email machadofisher@gmail.com
 */

public class NotImplemented extends RuntimeException  
{

    public NotImplemented() {
    }

    public NotImplemented(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplemented(String message) {
        super(message);
    }

    public NotImplemented(Throwable cause) {
        super(cause);
    }
}
