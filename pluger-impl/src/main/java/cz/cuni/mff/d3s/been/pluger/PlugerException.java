package cz.cuni.mff.d3s.been.pluger;

/**
 * Created by donarus on 8.3.15.
 */
public class PlugerException extends Exception {

    PlugerException() {
        super();
    }

    PlugerException(String message) {
        super(message);
    }

    PlugerException(String message, Throwable cause) {
        super(message, cause);
    }

    PlugerException(Throwable cause) {
        super(cause);
    }

}
