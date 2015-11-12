public enum Failure {
    UNKNOWN("00", "Unknown Error: An unknown error occurred. This was likely caused by an uncaught exception."),
    COOKIE_TIMEOUT("05", "Cookie Timeout Error: Your login cookie has timed out."),
    FORMAT_COMMAND("10", "Format Command Error: The specified client command isn't formatted properly."),
    UNKNOWN_COMMAND("11" ,"Unknown Command Error: The specified client command doesn't exist."),
    USERNAME_LOOKUP("20", "Username Lookup Error: The specified user does not exist."),
    AUTHENTICATION("21", "Authentication Error: The given password is not correct for the specified user."),
    USER("22", "User Error: The user cannot be created because the username has already been taken."),
    LOGIN("23", "Login Error: The specified user has not logged in to the server."),
    INVALID_VALUE("24", "Invalid Value Error: One of the specified values is logically invalid."),
    USER_CONNECTED("25", "User Connected Error: The specified user is already logged in the server.");

    private String code;
    private String message;

    Failure(String code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "FAILURE\t" + code + "\t" + message + "\r\n";
    }
}
