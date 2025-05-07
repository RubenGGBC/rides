package exceptions;

public class MonederoNoExisteException extends Exception {
	  private static final long serialVersionUID = 1L;
	    
	    public MonederoNoExisteException() {
	        super();
	    }
	    
	    public MonederoNoExisteException(String message) {
	        super(message);
	    }

}
