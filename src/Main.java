import server.Server;

public class Main {

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server(port, 256, "/var/www/html");
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.run();
    }
}
