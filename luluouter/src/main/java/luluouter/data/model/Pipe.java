package luluouter.data.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

import luluouter.data.util.SocketUtil;

public class Pipe implements Runnable {
    private final static int BUFFER_LENGTH = 5 * 1024 * 1024;
    private static AtomicLong index = new AtomicLong();

    private InputStream input;
    private OutputStream output;
    private String name;
    private volatile boolean flag = false;

    public Pipe(InputStream inputStream, OutputStream outputStream, String name) {
        this.input = inputStream;
        this.output = outputStream;
        this.name = index.getAndIncrement() + "/" + name;
        System.out.println(this.name);
        flag = true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            while (flag) {
                int length = input.read(buffer);
                System.out.println("receive " + name + " length:" + length);
                if (length > 0) {
                    output.write(buffer, 0, length);
                    output.flush();
                    continue;
                }
                flag = false;
            }
        } catch (IOException e) {
            System.out.println("Pipe.IOException: " + name + "/" + e.getMessage());
            // e.printStackTrace();
        } finally {
            System.out.println("Pipe.finally: " + name);
            SocketUtil.close(input);
            SocketUtil.close(output);
        }
    }

}
