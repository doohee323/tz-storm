package example7.tzstorm.zmq;

import java.io.BufferedReader;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import backtype.storm.utils.Utils;

public class ZMQPub {
	static final Logger log = LoggerFactory.getLogger(ZMQPub.class);

	private Context context = null;
	private Socket pub = null;
	private final String SUBID = "SUBID";

	public ZMQPub(String url) throws Exception {
		context = ZMQ.context(1);
		pub = context.socket(ZMQ.PUB);
		pub.bind(url);
	}

	public void send(final String message) {
		pub.sendMore("");
		pub.send("");
		pub.sendMore(SUBID);
		pub.send(message);
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
				Utils.sleep(100);
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
			ZMQPub client = new ZMQPub("tcp://*:5563");
			client.sendLog();
			Utils.sleep(100000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main (String[] args) throws Exception {
	// // Prepare our context and publisher
	// Context context = ZMQ.context(1);
	// Socket pub = context.socket(ZMQ.PUB);
	//
	// pub.bind("tcp://*:5563");
	// while (!Thread.currentThread ().isInterrupted ()) {
	// // Write two messages, each with an envelope and content
	// pub.sendMore ("A");
	// pub.send ("We don't want to see this");
	// pub.sendMore ("B");
	// pub.send("We would like to see this");
	// }
	// pub.close ();
	// context.term ();
	// }
}
