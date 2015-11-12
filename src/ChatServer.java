import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

	public ChatServer(User[] users, int maxMessages) {
		// TODO: Complete the constructor
		this.users = Arrays.asList(users);
		this.users.add(0, new User("root", "cs180", new SessionCookie(generateUniqueID())));
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
					return addUser(args);
				}
				return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
			case "USER-LOGIN":
				if(2 < args.length) {
					return userLogin(args);
				}
                return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
			case "POST-MESSAGE":
				if(2 < args.length) {
					// TODO: Add funcationality to get user name from cookie id
					return postMessage(args, null);
				}
                return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
			case "GET-MESSAGES":
				if(2 < args.length) {
					return getMessages(args);
				}
                return MessageFactory.makeErrorMessage(MessageFactory.FORMAT_COMMAND_ERROR);
			default:
                return MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_COMMAND_ERROR);
		}
	}

	public String addUser(String[] args) {
		// TODO: Add functionality to create new user
		return null;
	}

	public String userLogin(String[] args) {
		for(User user : users){
			if(user.getName().equals(args[1])){
				if(user.checkPassword(args[2])){
					// TODO: Complete user-login checks and updating
					return "SUCCESS\r\n";
				} else {
					return Failure.AUTHENTICATION.toString();
				}
			}
		}
		return Failure.USERNAME_LOOKUP.toString();
	}

	public String postMessage(String[] args, String name) {
		// TODO: Add functionality to post message
		return null;
	}

	public String getMessages(String[] args) {
		// TODO: Add functionality to get messages
		return null;
	}
}
