package example6.tzstorm.spout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

public class LogEmitter implements Emitter<Long> {
    private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);

    private List<String> logData = new ArrayList<String>();

    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
        for (String log : logData) {
            List<Object> oneTuple = Arrays.<Object> asList(log);
            collector.emit(oneTuple);
        }
        this.prepareData();
    }
    
    private void prepareData() {
        try {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket pull = null;
            try {
                pull = context.socket(ZMQ.PULL);
                pull.bind("tcp://127.0.0.1:9999");
                while (true) {
                    try {
                        String input = new String(pull.recv(0));
//                        System.out.println(input);
                        logData.add(input);
                    } catch (Exception e) {
                        System.out.println(e);
                        break;
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