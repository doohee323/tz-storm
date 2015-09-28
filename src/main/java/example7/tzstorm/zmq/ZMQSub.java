package example7.tzstorm.zmq;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import backtype.storm.utils.Utils;

public class ZMQSub extends Thread {
	private static final Logger log = LoggerFactory.getLogger(ZMQSub.class);
	private static ZMQSub singleton = null;
	private boolean start = false;
	private final String SUBID = "SUBID";

	public static ZMQSub getInstance() {
		if (singleton == null) {
			singleton = new ZMQSub();
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
	private ZMQ.Socket sub = null;

	public ZMQSub() {
		try {
			context = ZMQ.context(1);
			sub = context.socket(ZMQ.SUB);
			sub.setLinger(0L);
			sub.connect("tcp://192.168.1.4:5563");
		} catch (ZMQException e) {
			throw new RuntimeException(e.getCause());
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}

	public void run() {
		start = true;
		sub.subscribe(SUBID.getBytes());
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String input = new String(sub.recv(0));
				if (!input.equals(SUBID)) {
					log.error(input);
					logData.add(input);
				}
			} catch (Exception e) {
				System.out.println(e);
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
			ZMQSub.getInstance().start();
			Utils.sleep(100000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main (String[] args) {
	//
	// // Prepare our context and subscriber
	// ZMQ.Context context = ZMQ.context(1);
	// ZMQ.Socket sub = context.socket(ZMQ.SUB);
	//
	// sub.connect("tcp://localhost:5563");
	// sub.subscribe("B".getBytes());
	// while (!Thread.currentThread ().isInterrupted ()) {
	// // Read envelope with address
	// String address = sub.recvStr ();
	// // Read message contents
	// String contents = sub.recvStr ();
	// System.out.println(address + " : " + contents);
	// }
	// sub.close ();
	// context.term ();
	// }
}