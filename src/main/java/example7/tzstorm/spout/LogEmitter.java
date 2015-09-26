package example7.tzstorm.spout;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;
import example7.tzstorm.zmq.ZMQSub;

public class LogEmitter implements Emitter<Long> {
    private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);

    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
//        log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
        if(!ZMQSub.getInstance().isStart()) {
            ZMQSub.getInstance().start();
        }
        if(ZMQSub.getInstance().getLogData() != null) {
            for (String input : ZMQSub.getInstance().getLogData()) {
                log.error("got from zmq:" + input);
                List<Object> oneTuple = Arrays.<Object> asList(input);
                collector.emit(oneTuple);
            }
        }
    }

    public void success(TransactionAttempt tx) {
//        log.debug("Emitter.success({})", tx);
    }

    public void close() {
//        log.debug("Emitter.close()");
    }
}