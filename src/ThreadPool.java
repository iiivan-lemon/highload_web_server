/**
 * @author Paul Hinojosa, perseus086
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class ThreadPool {
    private Queue queue = null;
    private Worker[] workerThread = new Worker[20];
    private Receiver reception = null;
    private Scanner keyboard;
    private int numberOfWorkers;
    private int port;
    private ServerSocket serverSocket;

    public ThreadPool(int numberOfWorkers, int maxBufferSize, int port) {
        this.numberOfWorkers = numberOfWorkers;
        this.queue = new Queue(maxBufferSize);
        this.port = port;
    }

    public synchronized void initialize() throws IOException {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.exit(2);
        }


        for (int i = 0; i < numberOfWorkers; i++)
            workerThread[i] = new Worker(this.queue);
        for (int i = 0; i < numberOfWorkers; i++)
            workerThread[i].start();

        this.reception = new Receiver(this.serverSocket, this.queue);
        reception.start();

        String command = "";
        keyboard = new Scanner(System.in);

        while (!command.equals("quit")) {
            command = keyboard.nextLine().toLowerCase();
        }


        reception.stopRunning();
        for (int i = 0; i < numberOfWorkers; i++) {
            workerThread[i].stopRunning();

        }

        serverSocket.close();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        System.exit(0);
    }
}
