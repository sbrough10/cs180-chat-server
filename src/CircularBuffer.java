import java.util.List;

/**
 * Created by stav on 11/19/2015.
 */
public class CircularBuffer {
    private int size;
    private String[] messages;
    private int tail = 0;
    private int msgNum = 0;
    private String str = "";
    private int numAvailable = 0;

    public CircularBuffer(int size) {
        messages = new String[size];
    }

    public void put(String message) {
        msgNum %= 10000;
        messages[tail] = String.format("%03d) %s", msgNum, message);
        tail++;
        tail %= message.length();
        if (numAvailable < size) {
            numAvailable++;
        }
    }

    public String[] getNewest(int numMessages) {
        if (numMessages < 0) {
            return null;
        }
        String[] newest = new String[numMessages];
        if (numMessages <= numAvailable) {
            for (int i = 0; i < numMessages - 1; i++) {
                newest[i] = messages[numAvailable - numMessages + i];
            }
        } else {
            for (int i = 0; i < numMessages - 1; i++) {
                newest[i] = messages[i];
            }
        }
        return newest;
    }
}
