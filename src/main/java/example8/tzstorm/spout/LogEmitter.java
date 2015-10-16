package example8.tzstorm.spout;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;
import example8.tzstorm.LogBean;
import example8.tzstorm.graphite.GraphiteClient;

public class LogEmitter implements Emitter<Long> {
    static final Logger log = LoggerFactory.getLogger(LogEmitter.class);

    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        // log.debug("Emitter.emitBatch({}, {}, collector)", tx,
        // coordinatorMeta);
        GraphiteClient client = new GraphiteClient();
        List<String> logData = client.getData();
        for (String str : logData) {
            log.info(str);
            List<Object> oneTuple = Arrays.<Object> asList(str);
            collector.emit(oneTuple);
        }
    }

    public void success(TransactionAttempt tx) {
        // log.debug("Emitter.success({})", tx);
    }

    public void close() {
        // log.debug("Emitter.close()");
    }
}