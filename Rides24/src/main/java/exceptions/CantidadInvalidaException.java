package exceptions;

public class CantidadInvalidaException extends Exception {
	 private static final long serialVersionUID = 1L;
	    
	    public CantidadInvalidaException() {
	        super();
	    }
	    
	    public CantidadInvalidaException(String message) {
	        super(message);
	    }

}
