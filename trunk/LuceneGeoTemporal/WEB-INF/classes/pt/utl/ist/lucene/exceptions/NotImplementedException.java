package pt.utl.ist.lucene.exceptions;

/**
 * @author Jorge Machado
 * @date 30/Dez/2009
 * @time 9:22:20
 * @email machadofisher@gmail.com
 */
public class NotImplementedException extends RuntimeException
{

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }
}
