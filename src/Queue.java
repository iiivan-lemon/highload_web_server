import java.util.ArrayList;
import java.util.List;

public class Queue {

    //Buffer size
    private int bufferSize;

    //Buffer list
    private List<Object> buffer = new ArrayList<Object>();

    //Constructor of the queue
    public Queue(int bufferSize) {
        if (bufferSize <= 0) throw new IllegalArgumentException("Size is illegal");

        this.bufferSize = bufferSize;
    }

    //Buffer is full
    public synchronized boolean isFull() {
        return buffer.size() == bufferSize;
    }

    //Buffer is empty
    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }

    //Enqueue
    public synchronized void enqueue(Object obj) {
        while (isFull()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        //-->Add to the buffer
        buffer.add(obj);
        notifyAll();
    }

    public synchronized Object dequeue() {
        while (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        //-->Dequeue first message in the queue
        Object obj = buffer.remove(0);
        notifyAll();
        return obj;
    }
}
