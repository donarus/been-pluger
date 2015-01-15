package cz.cuni.mff.d3s.been.pluginator;

public class MissingConstructorException extends RuntimeException {

    MissingConstructorException(String message, Throwable reason) {
        super(message, reason);
    }

    MissingConstructorException(String message) {
        super(message);
    }

    MissingConstructorException(Throwable reason) {
        super(reason);
    }

}
