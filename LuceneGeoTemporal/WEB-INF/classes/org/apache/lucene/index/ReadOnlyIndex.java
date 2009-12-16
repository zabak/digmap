package org.apache.lucene.index;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 13:00:32
 * @email machadofisher@gmail.com
 */

public class ReadOnlyIndex extends RuntimeException  
{

    public ReadOnlyIndex() {
    }

    public ReadOnlyIndex(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadOnlyIndex(String message) {
        super(message);
    }

    public ReadOnlyIndex(Throwable cause) {
        super(cause);
    }
}
