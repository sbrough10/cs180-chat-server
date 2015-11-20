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
        if (msgNum < 10) {
            str = "000" + msgNum + ": " + message;
        }
        if (msgNum < 100) {
            str = "00" + msgNum + ": " + message;
        }
        if (msgNum < 1000) {
            str = "0" + msgNum + ": " + message;
        }
        if (msgNum < 10000) {
            str = "" + msgNum + ": " + message;
        }
        messages[tail] = str;
        if (tail < messages.length - 1) {
            tail++;
        } else {
            tail = 0;
        }
        if (numAvailable < size) {
            numAvailable++;
        }
    }

    public String[] getNewest(int numMessages) {
        String[] newest = new String[numMessages];
        if (numMessages < numAvailable) {
            for ()
        }
    }
}
