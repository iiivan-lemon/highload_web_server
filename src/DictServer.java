import java.io.IOException;

public class DictServer {

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.exit(0);
//        }

        int port = Integer.valueOf(args[0]);
        int numberOfThreads = 4;
        int bufferSize = 100;

        ThreadPool pool = new ThreadPool(numberOfThreads, bufferSize, port);
        pool.initialize();
    }
}
