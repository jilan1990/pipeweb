package luluouter.msg.inner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSON;

import luluouter.data.inner.InnerDataMaster;
import luluouter.msg.outer.ProxyServerMaster;

public class InnerMsgClient implements Runnable {
    private final static int FLAG_MSG_GET_PORT = 1024;
    private final static int FLAG_MSG_CREATE_DATA_PIPE = 2048;
    private final static int FLAG_MSG_HEART_BEAT = 1986;
    private final static String MSG_KEY_HEART_BEAT = "heartBeat";

    private BlockingQueue<Map<String, Object>> outQueue = new LinkedBlockingQueue<Map<String, Object>>();

    private int proxyPort;
    private final Socket inner;
    private final String key;

    private volatile boolean stop = true;

    public InnerMsgClient(Socket inner) {
        this.inner = inner;
        key = String.valueOf(inner.getRemoteSocketAddress());
        stop = false;
        System.out.println("InnerMsgClient:" + key);
    }

    @Override
    public void run() {
        try (OutputStream outputStream = inner.getOutputStream();
                DataOutputStream out = new DataOutputStream(outputStream);
                InputStream inputStream = inner.getInputStream();
                DataInputStream in = new DataInputStream(inputStream);) {
            while (!stop) {
                Map<String, Object> msg = outQueue.take();
                if (!msg.containsKey(MSG_KEY_HEART_BEAT)) {
                    System.out.println("InnerMsgClient.take:" + msg);
                }
                String json = JSON.toJSONString(msg);
                out.writeUTF(json);
                out.flush();
                if (!msg.containsKey(MSG_KEY_HEART_BEAT)) {
                    System.out.println("InnerMsgClient.writeUTF:" + json);
                }

                String line = in.readUTF();
                if (!line.contains(MSG_KEY_HEART_BEAT)) {
                    System.out.println("InnerMsgClient.readUTF:" + line);
                }
                Map<String, Object> result = JSON.parseObject(line, Map.class);
                dealMsg(result);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ProxyServerMaster.getInstance().removeInnerMsgClient(proxyPort, key);
            System.out.println("InnerMsgClient.finally:" + proxyPort + "/" + key);
        }
    }

    public void init() {
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgType", FLAG_MSG_GET_PORT);
        addMsg(msg);
        System.out.println("InnerMsgClient.init:" + msg);
    }

    public void addMsg(Map<String, Object> msg) {
        boolean result = outQueue.offer(msg);
        System.out.println("InnerMsgClient.outQueue.offer:" + result + "/" + msg);
    }

    private void dealMsg(Map<String, Object> msg) {
        Integer msgType = (Integer) msg.get("msgType");
        if (msgType == FLAG_MSG_GET_PORT) {
            proxyPort = (Integer) msg.get("proxyPort");
            ProxyServerMaster.getInstance().addInnerClient(proxyPort, this);
        } else if (msgType == FLAG_MSG_CREATE_DATA_PIPE) {
            Object indexObj = msg.get("index");
            String indexStr = String.valueOf(indexObj);
            Long index = Long.parseLong(indexStr);

            String result = (String) msg.get("result");
            if ("success".equals(result)) {
                InnerDataMaster.getInstance().startPipes(index);
            } else {
                InnerDataMaster.getInstance().destroyOuterClient(index);
            }
        }
    }

    public void createPipes(Socket outerClient) {
        long theIndex = InnerDataMaster.getInstance().addOuterClient(outerClient);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgType", FLAG_MSG_CREATE_DATA_PIPE);
        msg.put("index", theIndex);
        addMsg(msg);
    }

    public String getKey() {
        return key;
    }

    public void heartBeat() {
        long ts = System.currentTimeMillis();
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgType", FLAG_MSG_HEART_BEAT);
        msg.put("date", new Date(ts));
        msg.put(MSG_KEY_HEART_BEAT, ts);
        outQueue.offer(msg);
        // System.out.println("InnerMsgClient.heartBeat:" + msg);
    }
}
