import java.util.List;

/**
 * Created by stav on 11/19/2015.
 */
public class CircularBuffer {
    private int size;
    private String[] messages;
    private int tail = 0;
    private int msgNum = 0;
    private int numAvailable = 0;

    public CircularBuffer(int size) {
        this.size = size;
        messages = new String[size];
    }

    public void put(String message) {
        messages[tail] = String.format("%04d) %s", msgNum, message);
        msgNum = (msgNum + 1) % 10000;
        tail = (tail + 1) % size;
        if (numAvailable < size) {
            numAvailable++;
        }
    }

    public String[] getNewest(int numMessages) {
        if (numMessages < 1) {
            return null;
        }
        String[] newest = new String[Math.min(numMessages, numAvailable)];
        int start = size + tail - newest.length;
        for (int i = 0; i < newest.length; i++) {
            newest[i] = messages[(start + i) % size];
        }
        return newest;
    }
}
