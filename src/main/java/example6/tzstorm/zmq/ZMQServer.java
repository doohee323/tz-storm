package example6.tzstorm.zmq;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class ZMQServer {
    static final Logger log = LoggerFactory.getLogger(ZMQServer.class);

    public ZMQServer(String url) throws Exception {
        ZMQ.Context context = ZMQ.context(1);
        ByteBuffer bb = ByteBuffer.allocateDirect(5).order(ByteOrder.nativeOrder());
        ZMQ.Socket pull = null;
        try {
            pull = context.socket(ZMQ.PULL);
            pull.bind(url);
            int size = pull.recvByteBuffer(bb, 0);
            bb.flip();
            byte[] b = new byte[size];
            bb.get(b);
            System.out.println(new String(b));
        } finally {
            try {
                pull.close();
            } catch (Exception ignore) {
            }
            try {
                context.term();
            } catch (Exception ignore) {
            }
        }
    }

    public static void main(String args[]) {
        try {
            new ZMQServer("tcp://127.0.0.1:9999");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
