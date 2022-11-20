import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerPool {

    public static void main(String[] args) throws IOException {

        int echoServPort = 3030;
        int threadPoolSize = 15;

        final ServerSocket servSock = new ServerSocket(echoServPort);


        // Create and start threadPoolSize new threads
        for (int i = 0; i < threadPoolSize; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        try (Socket clntSock = servSock.accept()) {
                            (new RequestHandler(clntSock, "/var/www/html")).run();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}
