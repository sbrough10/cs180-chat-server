public class SessionCookie {

    public static int timeoutLength = 300;

    private long id;
    private long lastActivity;

    public SessionCookie(long id){
        this.id = id;
        updateTimeOfActivity();
    }

    public boolean hasTimedOut(){
        return timeoutLength * 1000 <= System.currentTimeMillis() - lastActivity;
    }

    /**
     * Updates the cookie's time of last activity by setting it to the current time.
     */
    public void updateTimeOfActivity() {
        lastActivity = System.currentTimeMillis();
    }

    /**
     * Returns the ID of the cookie.
     *
     * @return the id of the cookie
     */
    public long getID() {
        return id;
    }
}
