import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


public class ServerPool {
    private static final String CONFIG_PATH = "etc/httpd.conf";

    public static void main(String[] args) throws IOException {

        int port = 3030;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int threadLimit = Integer.parseInt(properties.getProperty("thread_limit", "1"));
        String documentRoot = properties.getProperty("document_root", "/var/www/html");

        final ServerSocket servSock = new ServerSocket(port);


        // Create and start threadPoolSize new threads
        for (int i = 0; i < threadLimit; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        try (Socket clntSock = servSock.accept()) {
                            (new RequestHandler(clntSock, documentRoot)).run();
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
