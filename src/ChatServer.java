import com.sun.deploy.util.StringUtils;

import java.util.*;

/**
 * <b> CS 180 - Project 4 - Chat Server Skeleton </b>
 * <p>
 *
 * This is the skeleton code for the ChatServer Class. This is a private chat
 * server for you and your friends to communicate.
 *
 * @author (Your Name) <(YourEmail@purdue.edu)>
 *
 * @lab (Your Lab Section)
 *
 * @version (Today's Date)
 *
 */
public class ChatServer {

	private List<User> users;
	private Random idGenerator = new Random();
    private CircularBuffer msgBuffer;

	public ChatServer(User[] users, int maxMessages) {
		this.users = Arrays.asList(users);
		this.users.add(0, new User("root", "cs180", null));
        msgBuffer = new CircularBuffer(maxMessages);
	}

	private long generateUniqueID(){
		long id;
		boolean unique;
		do {
			id = Math.abs(idGenerator.nextLong()) % 10000;
			unique = true;
			for (User user : users) {
				if (user.getCookie().getID() == id) {
					unique = false;
					break;
				}
			}
		} while (!unique);
		return id;
	}

    private User getUser(String cookieId){
        for(User user : users){
            SessionCookie cookie = user.getCookie();
            if(cookie != null && !cookie.hasTimedOut() && cookie.getID() == Long.parseLong(cookieId)) {
                return user;
            }
        }
        return null;
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
	 * Replaces "poorly formatted" escape characters with their proper values.
	 * For some terminals, when escaped characters are entered, the terminal
	 * includes the "\" as a character instead of entering the escape character.
	 * This function replaces the incorrectly inputed characters with their
	 * proper escaped characters.
	 *
	 * @param str
	 *            - the string to be edited
	 * @return the properly escaped string
	 */
	private static String replaceEscapeChars(String str) {
		str = str.replace("\\r", "\r");
		str = str.replace("\\n", "\n");
		str = str.replace("\\t", "\t");

		return str;
	}

	/**
	 * Determines which client command the request is using and calls the
	 * function associated with that command.
	 *
	 * @param request
	 *            - the full line of the client request (CRLF included)
	 * @return the server response
	 */
	public String parseRequest(String request) {
		String[] args = request.split("\t");
		switch(args[0]){
			case "ADD-USER":
				if(3 < args.length) {
                    if(getUser(args[1]) != null) {
                        return addUser(args);
                    }
                    return MessageFactory.makeErrorMessage(MessageFactory.COOKIE_TIMEOUT_ERROR);
				}
				break;
			case "USER-LOGIN":
				if(2 < args.length) {
                    return userLogin(args);
				}
                break;
			case "POST-MESSAGE":
				if(2 < args.length) {
                    User user = getUser(args[1]);
                    if(user != null) {
                        return postMessage(args, user.getName());
                    }
                    return MessageFactory.makeErrorMessage(MessageFactory.COOKIE_TIMEOUT_ERROR);
				}
                break;
			case "GET-MESSAGES":
				if(2 < args.length) {
                    if(getUser(args[1]) != null) {
					    return getMessages(args);
                    }
                    return MessageFactory.makeErrorMessage(MessageFactory.COOKIE_TIMEOUT_ERROR);
				}
                break;
			default:
                return MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_COMMAND_ERROR);
		}
        return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
	}

	public String addUser(String[] args) {
		for(User user : users){
            if(user.getName().equals(args[2])){
                return MessageFactory.makeErrorMessage(MessageFactory.USER_ERROR);
            }
        }
        users.add(new User(args[2], args[3], null));
		return "SUCCESS\r\n";
	}

	public String userLogin(String[] args) {
		for(User user : users){
			if(user.getName().equals(args[1])){
				if(user.checkPassword(args[2])){
                    SessionCookie cookie = user.getCookie();
                    if(cookie == null || cookie.hasTimedOut()) {
                        long id = generateUniqueID();
                        user.setCookie(new SessionCookie(id));
                        return String.format("SUCCESS\t%03d\n", id);
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
		msgBuffer.put(name + ": " + args[2]);
		return "SUCCESS\r\n";
	}

	public String getMessages(String[] args) {
        String[] msgList = msgBuffer.getNewest(Integer.parseInt(args[2]));
        if(msgList == null){
            return MessageFactory.makeErrorMessage(MessageFactory.INVALID_VALUE_ERROR);
        }
		return "SUCCESS\t" + StringUtils.join(Arrays.asList(), "\t") + "\r\n";
	}
}
