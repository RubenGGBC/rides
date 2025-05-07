package exceptions;

public class SaldoInsuficienteException extends Exception {
	 private static final long serialVersionUID = 1L;
	    
	    public SaldoInsuficienteException() {
	        super();
	    }
	    
	    public SaldoInsuficienteException(String message) {
	        super(message);
	    }
	}


