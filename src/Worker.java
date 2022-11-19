import java.net.Socket;

public class Worker extends Thread {

    private Queue queue;
    private volatile boolean isRunning;

    // Constructors
    public Worker(Queue queue) {
        this.queue = queue;
        this.isRunning = true;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        Socket socket;
        while (isRunning) {
            while (!queue.isEmpty()) {
                socket = (Socket) queue.dequeue();
                (new RequestHandler(socket, "/var/www/html")).run();
            }
        }
    }

    public synchronized void stopRunning() {
        this.isRunning = false;
    }
}
