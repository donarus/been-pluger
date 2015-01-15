package cz.cuni.mff.d3s.been.pluginator;

public class DescriptorException extends Exception {
    public DescriptorException(String msg) {
        super(msg);
    }
    public DescriptorException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public DescriptorException(Throwable cause) {
        super(cause);
    }
}
