package example6.tzstorm.spout;

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
import backtype.storm.utils.Utils;

public class LogEmitter implements Emitter<Long> {
    private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);
    private Long poll_interval = 100000L;
    
    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
        getData(collector);
    }

    private void getData(TridentCollector collector) {
        try {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket pull = context.socket(ZMQ.PULL);
            try {
                pull.setLinger(0L);
                pull.bind("tcp://127.0.0.1:9999");

                Poller poller = context.poller(1);
                poller.register(pull, Poller.POLLIN);

                if (0 != poller.poll(poll_interval)) {
                    if (poller.pollin(0)) {
                        try {
                            String input = new String(pull.recv(0));
                            log.error(input);
                             List<Object> oneTuple = Arrays.<Object> asList(input);
                             Utils.sleep(100);
                             collector.emit(oneTuple);
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