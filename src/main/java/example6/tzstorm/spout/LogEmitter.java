package example6.tzstorm.spout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQException;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

import com.google.gson.Gson;

public class LogEmitter implements Emitter<Long> {
	private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);
	private List<String> logData = new ArrayList<String>();

	private static final Gson gson = new Gson();

	public void emitBatch(TransactionAttempt tx, Long coordinatorMeta,
			TridentCollector collector) {
		log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
		getData(collector);
//		for (String input : logData) {
//			List<Object> oneTuple = Arrays.<Object> asList(input);
//			collector.emit(oneTuple);
//		}
	}

	private void getData(TridentCollector collector) {
		try {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket pull = context.socket(ZMQ.PULL);
			try {
				pull.setLinger(0L);
				pull.bind("tcp://127.0.0.1:9999");
				
//				for(int i=0;i<10;i++) {
//					String input = new String(pull.recv(0));
//					List<Object> oneTuple = Arrays.<Object> asList(input);
//					collector.emit(oneTuple);
//				}
				
				Poller poller = context.poller();
				poller.register(pull, Poller.POLLIN);

				if (0 != poller.poll()) {
					if (poller.pollin(0)) {
						try {
							String input = new String(pull.recv(0));
							System.out.println(input);
							logData.add(input);
						} catch (Exception e) {
							System.out.println(e);
						}
					}
				}
			} finally {
				try {
					pull.close();
					context.term();
				} catch (Exception ignore) {
				}
			}
		} catch (ZMQException e) {
			throw new RuntimeException(e.getCause());
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}

	public void success(TransactionAttempt tx) {
		log.debug("Emitter.success({})", tx);
	}

	public void close() {
		log.debug("Emitter.close()");
	}

}