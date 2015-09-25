package example6.tzstorm.zmq;

import java.io.BufferedReader;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import backtype.storm.utils.Utils;
import ch.qos.logback.classic.pattern.Util;

public class ZMQClient {
    static final Logger log = LoggerFactory.getLogger(ZMQClient.class);

    private String _url;
    private Context context = null;
    private Socket socket = null;
    private StringBuilder sb = new StringBuilder(1024);

    private final static int VERSION = 1;

    public ZMQClient(String url) throws Exception {
        _url = url;
        context = ZMQ.context(1);
        socket = context.socket(ZMQ.PUSH);
        socket.setLinger(0);
        socket.setHWM(1L);
        //
        // attempt connect/reconnect at 1s, 2s, 4s, 8s, and then every 10s
        //
        socket.setReconnectIVL(1000L);
        socket.setReconnectIVLMax(10000L);
        socket.connect(url);
    }

    public boolean timing(final String key, final String values) {
        return send(key, values, "t");
    }

    public boolean increment(final String key) {
        return send(key, 1, "c");
    }

    public boolean increment(final String key, final int value) {
        return send(key, value, "c");
    }

    private boolean send(final String key, final String values, final String c) {
        synchronized (sb) {
            sb.setLength(0);
            sb.append(VERSION).append(";");
            sb.append(key).append(":").append(values).append("|").append(c);

            synchronized (socket) {
                return socket.send(sb.toString().getBytes(), 0);
            }
        }
    }

    private boolean send(final String key, final int value, final String c) {
        synchronized (sb) {
            sb.setLength(0);
            sb.append(VERSION).append(";");
            sb.append(key).append(":").append(value).append("|").append(c);

            synchronized (socket) {
                return socket.send(sb.toString().getBytes(), 0);
            }
        }
    }

    public boolean send(final String message) {
        synchronized (socket) {
            return socket.send(message.getBytes(), 0);
        }
    }

    public void sendLog() {
        String LOG_FILENAME = "data/a.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(LOG_FILENAME));
            String oneLine = br.readLine();
            if (oneLine == null) {
                br.close();
                throw new Exception("!!!");
            }

            int totalLines = 0;
            while (oneLine != null) {
                totalLines++;
//                Utils.sleep(1000);
                send(oneLine);
                oneLine = br.readLine();
            }
            log.debug("Lines in ten second log: " + totalLines);
            br.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            ZMQClient client = new ZMQClient("tcp://127.0.0.1:9999");
            client.sendLog();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}