package luluouter.data.inner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class InnerDataClient implements Runnable {
    private Socket inner;

    public InnerDataClient(Socket inner) {
        this.inner = inner;
    }


    @Override
    public void run() {
        try {
            InputStream inputStream = inner.getInputStream();
            byte[] bytes = new byte[8];
            inputStream.read(bytes);
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result <<= 8;
                result |= (bytes[i] & 0xFF);
            }
            InnerDataMaster.getInstance().addInnerDataSocket(result, inner);

            OutputStream outputStream = inner.getOutputStream();
            outputStream.write(66);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
