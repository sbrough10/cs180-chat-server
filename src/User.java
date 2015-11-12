
public class User {
	
	private String name;
    private String password;
    private SessionCookie cookie;

    public User(String name, String password, SessionCookie cookie){
        this.name = name;
        this.password = password;
        this.cookie = cookie;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the given password matches the user's password exactly. Otherwise, false is returned.
     *
     * @param password the password to check
     * @return whether given password matches user password
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Returns the user's session cookie.
     *
     * @return the cookie
     */
    public SessionCookie getCookie() {
        return cookie;
    }

    /**
     * Updates the user's session cookie with the specified parameter.
     *
     * @param cookie the cookie
     */
    public void setCookie(SessionCookie cookie) {
        this.cookie = cookie;
    }
}
