import java.util.*;
import java.util.function.Function;

/**
 * <b> CS 180 - Project 4 - Chat Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the ChatServer Class. This is a private chat server for you and your friends to
 * communicate.
 *
 * @author (Your Name) <(YourEmail@purdue.edu)>
 * @version (Today's Date)
 * @lab (Your Lab Section)
 */
public class ChatServer {

    private List<User> users;
    private Random idGenerator = new Random();
    private CircularBuffer msgBuffer;

    public ChatServer(User[] users, int maxMessages) {
        this.users = new LinkedList<>();
        this.users.add(new User("root", "cs180", null));
        for (User user : users) {
            this.users.add(user);
        }
        msgBuffer = new CircularBuffer(maxMessages);
    }

    private long generateUniqueID() {
        long id;
        boolean unique;
        do {
            id = Math.abs(idGenerator.nextLong()) % 10000;
            unique = true;
            for (User user : users) {
                if (user.getCookie() != null && user.getCookie().getID() == id) {
                    unique = false;
                    break;
                }
            }
        } while (!unique);
        return id;
    }

    private String validateCookie(String cookieId, Function<User, String> action) {
        for (User user : users) {
            SessionCookie cookie = user.getCookie();
            if (cookie != null && cookie.getID() == Long.parseLong(cookieId)) {
                if (cookie.hasTimedOut()) {
                    user.setCookie(null);
                    return MessageFactory.makeErrorMessage(MessageFactory.COOKIE_TIMEOUT_ERROR);
                }
                return action.apply(user);
            }
        }
        return MessageFactory.makeErrorMessage(MessageFactory.LOGIN_ERROR);
    }

    /**
     * This method begins server execution.
     */
    public void run() {
        boolean verbose = false;
        System.out.printf("The VERBOSE option is off.\n\n");
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            // this allows students to manually place "\r\n" at end of command
            // in prompt
            command = replaceEscapeChars(command);

            if (command.startsWith("kill"))
                break;

            if (command.startsWith("verbose")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getMessage()));
            }

            // change the formatting of the server response so it prints well on
            // the terminal (for testing purposes only)
            if (response.startsWith("SUCCESS\t"))
                response = response.replace("\t", "\n");

            // print the server response
            if (verbose)
                System.out.printf("response:\n");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper values. For some terminals, when escaped
     * characters are entered, the terminal includes the "\" as a character instead of entering the escape character.
     * This function replaces the incorrectly inputed characters with their proper escaped characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");

        return str;
    }

    /**
     * Determines which client command the request is using and calls the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {
        if (!request.endsWith("\r\n")) {
            return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
        }
        String[] args = request.trim().split("\t");
        switch (args[0]) {
            case "ADD-USER":
                if (4 == args.length) {
                    return validateCookie(args[1], user -> {
                        user.getCookie().updateTimeOfActivity();
                        return addUser(args);
                    });
                }
                break;
            case "USER-LOGIN":
                if (3 == args.length) {
                    return userLogin(args);
                }
                break;
            case "POST-MESSAGE":
                if (3 == args.length) {
                    return validateCookie(args[1], user -> {
                        user.getCookie().updateTimeOfActivity();
                        return postMessage(args, user.getName());
                    });
                }
                break;
            case "GET-MESSAGES":
                if (3 == args.length) {
                    return validateCookie(args[1], user -> {
                        return getMessages(args);
                    });
                }
                break;
            default:
                return MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_COMMAND_ERROR);
        }
        return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
    }

    public String addUser(String[] args) {
        if (!args[2].matches("^[A-Za-z0-9]{1,20}$") || !args[3].matches("^[A-Za-z0-9]{4,40}$")) {
            return MessageFactory.makeErrorMessage(MessageFactory.INVALID_VALUE_ERROR);
        }
        for (User user : users) {
            if (user.getName().equals(args[2])) {
                return MessageFactory.makeErrorMessage(MessageFactory.USER_ERROR);
            }
        }
        users.add(new User(args[2], args[3], null));
        return "SUCCESS\r\n";
    }

    public String userLogin(String[] args) {
        for (User user : users) {
            if (user.getName().equals(args[1])) {
                if (user.checkPassword(args[2])) {
                    SessionCookie cookie = user.getCookie();
                    if (cookie == null || cookie.hasTimedOut()) {
                        long id = generateUniqueID();
                        user.setCookie(new SessionCookie(id));
                        return String.format("SUCCESS\t%04d\r\n", id);
                    } else {
                        return MessageFactory.makeErrorMessage(MessageFactory.USER_CONNECTED_ERROR);
                    }
                } else {
                    return MessageFactory.makeErrorMessage(MessageFactory.AUTHENTICATION_ERROR);
                }
            }
        }
        return MessageFactory.makeErrorMessage(MessageFactory.USERNAME_LOOKUP_ERROR);
    }

    public String postMessage(String[] args, String name) {
        String message = args[2].trim();
        if (message.length() == 0) {
            return MessageFactory.makeErrorMessage(MessageFactory.INVALID_VALUE_ERROR);
        }
        msgBuffer.put(name + ": " + message);
        return "SUCCESS\r\n";
    }

    public String getMessages(String[] args) {
        int msgNum;
        try {
            msgNum = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            return MessageFactory.makeErrorMessage(MessageFactory.INVALID_VALUE_ERROR);
        }
        String[] msgArray = msgBuffer.getNewest(msgNum);
        if (msgArray == null) {
            return MessageFactory.makeErrorMessage(MessageFactory.INVALID_VALUE_ERROR);
        }
        return "SUCCESS\t" + Utils.join(msgArray, "\t") + "\r\n";
    }
}
