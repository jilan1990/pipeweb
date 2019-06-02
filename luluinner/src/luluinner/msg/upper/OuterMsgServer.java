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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import luluinner.pipe.Pipe;
import luluinner.util.Constants;
import luluinner.util.SocketUtil;

public class OuterMsgServer {
    private final static int FLAG_MSG_HELLO = 2017;
    private final static int FLAG_MSG_CREATE_DATA_PIPE = 2048;
    private final static String MSG_KEY_HEART_BEAT = "heartBeat";

    private final String outer_ip;
    private final int outer_msg_port;

    private final int outer_data_port;
    private final String pigeonCode;

    private volatile long lastHeartBeatTs = 0;

    public OuterMsgServer(String outer_ip, int outer_msg_port, String pigeonCode, int outer_data_port) {
        this.outer_ip = outer_ip;
        this.outer_msg_port = outer_msg_port;
        this.pigeonCode = pigeonCode;
        this.outer_data_port = outer_data_port;

        System.out.println("OuterMsgServer:" + outer_ip + "/" + outer_msg_port + "/" + pigeonCode);
    }

    public void init() {
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (isTimeOut()) {
                System.out.println(
                        "OuterMsgServer.restart.outerMsgServer:" + outer_ip + "/" + outer_msg_port + "/" + pigeonCode);
                openTelegraph();
            }
        }, 0, Constants.RESTART_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void openTelegraph() {
        try (Socket socket = new Socket(outer_ip, outer_msg_port);
                OutputStream outputStream = socket.getOutputStream();
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
            System.out.println("OuterMsgServer.finally:" + outer_ip + "/" + outer_msg_port + "/" + pigeonCode);
        }
    }

    private Map<String, Object> dealMsg(Map<String, Object> msg) {
        // msg
        Map<String, Object> result = new HashMap<String, Object>();

        Integer msgType = (Integer) msg.get("msgType");
        if (msgType == FLAG_MSG_HELLO) {
            result.put("msgType", FLAG_MSG_HELLO);
            result.put("code", pigeonCode);
        } else if (msgType == FLAG_MSG_CREATE_DATA_PIPE) {
            Object indexObj = msg.get("index");
            String indexStr = String.valueOf(indexObj);
            Long index = Long.parseLong(indexStr);

            String inner_ip = (String) msg.get("inner_ip");
            int inner_port = (Integer) msg.get("inner_port");

            result.put("msgType", FLAG_MSG_CREATE_DATA_PIPE);
            result.put("index", index);

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
