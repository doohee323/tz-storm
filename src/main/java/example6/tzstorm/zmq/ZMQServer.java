package example6.tzstorm.zmq;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQException;

import backtype.storm.utils.Utils;

public class ZMQServer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);
    private static ZMQServer singleton = null;
    private boolean start = false;
    
    public static ZMQServer getInstance() {
        if (singleton == null) {
            singleton = new ZMQServer();
        }
        return singleton;
    }

    private List<String> logData = new ArrayList<String>(1024);

    public List<String> getLogData() {
        return logData;
    }

    public void setLogData(List<String> logData) {
        this.logData = logData;
    }

    private ZMQ.Context context = null;
    private ZMQ.Socket pull = null;
    private Poller poller = null;
    private Long poll_interval = 100000L; // 10 ms

    public ZMQServer() {
        try {
            context = ZMQ.context(1);
            pull = context.socket(ZMQ.REP);
            pull.setLinger(0L);
            pull.bind("tcp://*:9999");

            poller = context.poller(1);
            poller.register(pull, Poller.POLLIN);
        } catch (ZMQException e) {
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public void run() {
        start = true;
        while (!Thread.currentThread().isInterrupted()) {
            if (0 != poller.poll(poll_interval)) {
                if (poller.pollin(0)) {
                    try {
                        String input = new String(pull.recv(0));
                        System.out.println(input);
                        log.error(input);
                        logData.add(input);
                        pull.send("RECIEVE".getBytes(), 0);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
            }
        }
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
    
    public static void main(String args[]) {
        try {
        	ZMQServer.getInstance().start();
            Utils.sleep(100000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }   

}