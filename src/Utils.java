public class Utils {
    public static String join(String[] strings, String delim) {
        if (strings.length == 0) {
            return "";
        }
        String joined = strings[0];
        for (int i = 1; i < strings.length; i++) {
            joined += delim + strings[i];
        }
        return joined;
    }
}
