package luluinner.msg.upper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;

import luluinner.pipe.Pipe;
import luluinner.util.Constants;
import luluinner.util.SocketUtil;

public class OuterMsgServer implements Runnable {
    private final static int FLAG_MSG_GET_PORT = 1024;
    private final static int FLAG_MSG_CREATE_DATA_PIPE = 2048;
    private final static String MSG_KEY_HEART_BEAT = "heartBeat";

    private Socket socket;
    private Map<String, Object> configs;
    private volatile long lastHeartBeatTs;

    public OuterMsgServer(Socket socket, Map<String, Object> configs) {
        this.socket = socket;
        this.configs = configs;
        lastHeartBeatTs = System.currentTimeMillis();
        System.out.println("OuterMsgServer:" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                DataInputStream in = new DataInputStream(inputStream);) {

            String line = null;
            while ((line = in.readUTF()) != null) {
                lastHeartBeatTs = System.currentTimeMillis();
                if (!line.contains(MSG_KEY_HEART_BEAT)) {
                    System.out.println("OuterMsgServer.readUTF:" + line);
                }
                Map<String, Object> msg = JSON.parseObject(line, Map.class);

                Map<String, Object> result = dealMsg(msg);

                String json = JSON.toJSONString(result);
                out.writeUTF(json);
                out.flush();
                if (!result.containsKey(MSG_KEY_HEART_BEAT)) {
                    System.out.println("OuterMsgServer.writeUTF:" + json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("OuterMsgServer.finally:" + configs);
        }
    }

    private Map<String, Object> dealMsg(Map<String, Object> msg) {
        // msg
        Map<String, Object> result = new HashMap<String, Object>();

        Integer msgType = (Integer) msg.get("msgType");
        if (msgType == FLAG_MSG_GET_PORT) {
            result.put("msgType", FLAG_MSG_GET_PORT);
            result.put("proxyPort", configs.get("inner_port"));
        } else if (msgType == FLAG_MSG_CREATE_DATA_PIPE) {
            Object indexObj = msg.get("index");
            String indexStr = String.valueOf(indexObj);
            Long index = Long.parseLong(indexStr);

            result.put("msgType", FLAG_MSG_CREATE_DATA_PIPE);
            result.put("index", index);

            String inner_ip = (String) configs.get("inner_ip");
            int inner_port = (Integer) configs.get("inner_port");

            String outer_ip = (String) configs.get("outer_ip");
            int outer_data_port = (Integer) configs.get("outer_data_port");

            try {
                Socket innerSocket = new Socket(inner_ip, inner_port);
                System.out.println("OuterMsgServer.dealMsg.innerSocket:" + innerSocket.getRemoteSocketAddress());

                Socket outerSocket = new Socket(outer_ip, outer_data_port);
                System.out.println("OuterMsgServer.dealMsg.outerSocket:" + outerSocket.getRemoteSocketAddress());

                OutputStream outputStream = outerSocket.getOutputStream();
                byte[] bytes = new byte[8];
                long theIndex = index;
                for (int i = 7; i >= 0; i--) {
                    bytes[i] = (byte) (theIndex & 0xFF);
                    theIndex >>= 8;
                }
                outputStream.write(bytes);
                outputStream.flush();

                InputStream inputStream = outerSocket.getInputStream();
                inputStream.read();

                startPipes(outerSocket, innerSocket);
                result.put("result", "success");
                result.put("outerSocket", outerSocket.getRemoteSocketAddress().toString());
            } catch (UnknownHostException e) {
                result.put("result", "failed");
                e.printStackTrace();
            } catch (IOException e) {
                result.put("result", "failed");
                e.printStackTrace();
            }
        } else {
            result.putAll(msg);
        }
        return result;
    }

    private void startPipes(Socket outerSocket, Socket innerSocket) {
        try {
            startPipe(
                    new Pipe(outerSocket.getInputStream(), innerSocket.getOutputStream(), "outerSocket->innerSocket"));
            startPipe(
                    new Pipe(innerSocket.getInputStream(), outerSocket.getOutputStream(), "innerSocket->outerSocket"));
        } catch (IOException e) {
            e.printStackTrace();
            SocketUtil.close(outerSocket);
            SocketUtil.close(innerSocket);
        }
    }

    private void startPipe(Pipe pipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(pipe);
        executor.shutdown();
    }

    public boolean isTimeOut() {
        return (System.currentTimeMillis() - lastHeartBeatTs) > Constants.RESTART_PERIOD;
    }
}
