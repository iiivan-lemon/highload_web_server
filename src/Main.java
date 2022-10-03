import server.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private static final String CONFIG_PATH = "etc/httpd.conf";

    public static void main(String[] args) {
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


        Server server = new Server(port, threadLimit, documentRoot);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.run();
    }
}
