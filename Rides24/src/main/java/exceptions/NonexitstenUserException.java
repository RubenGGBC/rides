package exceptions;
public class NonexitstenUserException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public NonexitstenUserException()
  {
    super();
  }

  public NonexitstenUserException(String s)
  {
    super(s);
  }
}