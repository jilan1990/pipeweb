package luluouter.data.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SocketUtil {

    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                socket = null;
                System.out.println("close Socket:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void close(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
                input = null;
                System.out.println("close InputStream:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void close(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                out = null;
                System.out.println("close OutputStream:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
