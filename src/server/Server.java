package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private final int serverPort;
    private final String path;
    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private final ThreadPool threadPool;

    public Server(int serverPort, int threadLimit, String path) {
        this.serverPort = serverPort;
        this.threadPool = new ThreadPool(threadLimit, threadLimit * 10);
        this.path = path;
    }


    public void run() {
        openServerSocket();

        while (!this.isStopped) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (this.isStopped) {
                    break;
                }
                throw new RuntimeException(e);
            }

            this.threadPool.addTask(new RequestHandler(clientSocket, path));
        }
        System.out.println("server stopped");
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
            this.threadPool.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            System.exit(-1);
        }
    }
}
