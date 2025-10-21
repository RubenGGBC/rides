package ejer1;

public class AuthService {
    public boolean signIn(String service, String log, String pass) { if (service.compareTo("facebook")==0)
        return singInWithFB(log, pass); if (service.compareTo("google")==0)
        return singInWithGoogle(log, pass); if (service.compareTo("twitter")==0)
        return singInWithTwitter(log, pass); return false;
    }
    public boolean singInWithFB(String log, String pass) {
        //use the FB api
        return true; }
    public boolean singInWithGoogle(String log, String pass) { //use the google api
        return true; }
    public boolean singInWithTwitter(String log, String pass) { //use the Twitter api
        return true; }
}
