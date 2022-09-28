package server;

import java.net.Socket;

class RequestHandler implements Runnable {
    protected Socket clientSocket;

    RequestHandler(Socket clientSocket, String path) {
        this.clientSocket = clientSocket;
        String defDir = System.getProperty("user.dir") + path;
    }

    public void run() {
    }

}
