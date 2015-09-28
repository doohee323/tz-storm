package example7.tzstorm.zmq;

import java.io.BufferedReader;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import backtype.storm.utils.Utils;

public class ZMQClient {
    static final Logger log = LoggerFactory.getLogger(ZMQClient.class);

    private Context context = null;
    private Socket socket = null;
    private StringBuilder sb = new StringBuilder(1024);

    private final static int VERSION = 1;

    public ZMQClient(String url) throws Exception {
        context = ZMQ.context(1);
        socket = context.socket(ZMQ.REQ);
        socket.setLinger(0);
        socket.setHWM(1L);
        //
        // attempt connect/reconnect at 1s, 2s, 4s, 8s, and then every 10s
        //
        socket.setReconnectIVL(1000L);
        socket.setReconnectIVLMax(10000L);
        socket.connect(url);
    }

    public void send(final String message) {
        synchronized (socket) {
            socket.send(message.getBytes(), 0);
            String reply = socket.recvStr(0);
            System.out.println("[" + reply + "]");
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
                if (oneLine != null && oneLine.length() > 0) {
                    send(oneLine);
                }
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
            Utils.sleep(100000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
