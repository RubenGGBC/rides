package exceptions;
public class UserAlredyExistException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public UserAlredyExistException()
  {
    super();
  }

  public UserAlredyExistException(String s)
  {
    super(s);
  }
}