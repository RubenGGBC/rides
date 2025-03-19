package exceptions;
public class AnyRidesException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public AnyRidesException()
  {
    super();
  }

  public AnyRidesException(String s)
  {
    super(s);
  }
}