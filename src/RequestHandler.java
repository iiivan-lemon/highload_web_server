import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;

class RequestHandler implements Runnable {
    protected Socket clientSocket;
    private static String DEFAULT_FILES_DIR;
    private final static String CRLF = "" + (char) 0x0D + (char) 0x0A;
    private static final int BUFFER_SIZE = 2048;

    RequestHandler(Socket clientSocket, String path) {
        this.clientSocket = clientSocket;
        DEFAULT_FILES_DIR = System.getProperty("user.dir") + path;
    }

    public void run() {
        InputStream in = null;
        OutputStream out = null;

        try {
            clientSocket.setSoTimeout(10000);
        } catch (SocketException e) {
            //e.printStackTrace();
        }
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            String readRequest = readRequest(in);
            String method;
            int to = readRequest.indexOf(" ");
            if (to == -1) {
                method = null;
            } else {
                method = readRequest.substring(0, to);
            }

            if (method == null)
                throw new IOException();

            switch (method) {
                case "HEAD": {
                    String url = getRequestURL(readRequest);
                    sendFile(url, out, true);
                    break;
                }
                case "GET": {
                    String url = getRequestURL(readRequest);
                    sendFile(url, out, false);
                    break;
                }
                default:
                    sendResponseHeader(out, 405, null, 0);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            if (in != null)
                in.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            if (out != null)
                out.close();
            else
                clientSocket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    private String readRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String str;
        while (true) {
            str = reader.readLine();
            if (str.isEmpty()) {
                break;
            }
            builder.append(str).append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private String getRequestURL(String header) {
        int from = header.indexOf(" ") + 1;
        if (from == 0)
            return DEFAULT_FILES_DIR + "/index.html";

        int to = header.indexOf(" ", from);
        if (to == -1)
            return DEFAULT_FILES_DIR + "/index.html";

        String uri = header.substring(from, to);
        uri = java.net.URLDecoder.decode(uri, StandardCharsets.UTF_8);
        if (uri.lastIndexOf("/") == uri.length() - 1)
            if (!uri.contains("."))
                return DEFAULT_FILES_DIR + uri + "index.html";
            else uri += "badPath";

        int paramIndex = uri.indexOf("?");
        if (paramIndex != -1)
            uri = uri.substring(0, paramIndex);

        if (checkURL(uri))
            return null;

        return DEFAULT_FILES_DIR + uri;
    }

    private void sendFile(String url, OutputStream out, Boolean isHead) {
        if (url == null) {
            sendResponseHeader(out, 403, null, 0);
            return;
        }
        int code = 200;
        String contentType = null;
        int contentSize = 0;

        try {
            File file = new File(url);
            contentType = getContentType(file);
            contentSize = (int) file.length();
            FileInputStream fin = new FileInputStream(file);

            sendResponseHeader(out, code, contentType, contentSize);

            if (!isHead) {
                int count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((count = fin.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
            }
            fin.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
            code = url.contains("/index.html") ? 403 : 404;
        }
        if (code != 200)
            sendResponseHeader(out, code, contentType, contentSize);
    }

    private void sendResponseHeader(OutputStream out, int code, String contentType, int contentSize) {
        String header;
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1 ").append(code).append(" ").append(getAnswer(code)).append(CRLF);
        buffer.append("Server: Java web-server" + CRLF);
        buffer.append("Connection: close" + CRLF);
        buffer.append("Date: ").append(new Date()).append(CRLF);
        if (code == 200) {
            if (contentType != null)
                buffer.append("Content-Type: ").append(contentType).append(CRLF);
            if (contentSize != 0)
                buffer.append("Content-Length: ").append(contentSize).append(CRLF);
        }
        buffer.append(CRLF);
        header = buffer.toString();


        PrintStream answer = new PrintStream(out, true, StandardCharsets.UTF_8);
        answer.print(header);
    }

    private String getContentType(File file) throws IOException {
        int index = file.getPath().lastIndexOf('.');
        if (index > 0) {
            if (file.getPath().substring(index + 1).equals("swf")) {
                return "application/x-shockwave-flash";
            }
        }

        return Files.probeContentType(file.toPath());
    }

    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method not allowed";
            default:
                return "Internal Server Error";
        }
    }

    private int checksubStr(String origin, String subStr) {
        int count = 0;
        while (origin.contains(subStr)) {
            origin = origin.replaceFirst(subStr, "");
            count++;
        }
        return count;
    }

    private Boolean checkURL(String url) {
        int s = checksubStr(url, "/..");
        if (s > 0) {
            int nesting = checksubStr(url, "/") - 2 * s;
            return nesting < 0;
        }
        return false;
    }
}
