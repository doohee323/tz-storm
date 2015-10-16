package example8.tzstorm.bolt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.espertech.esper.client.EPServiceProvider;
import com.google.gson.Gson;

import example8.tzstorm.LogBean;

@SuppressWarnings("rawtypes")
public class TestBolt8 implements Function {

    private static final long serialVersionUID = 1L;
    static final Logger log = LoggerFactory.getLogger(TestBolt8.class);
    private int partitionIndex;

    private static final Gson gson = new Gson();

    private EsperFunc esperFunc;
    private EPServiceProvider epService;

    public void prepare(Map conf, TridentOperationContext context) {
        // log.info("CountSumFunction.prepare(): partition[{}/{}]",
        // context.getPartitionIndex(), context.numPartitions());
        partitionIndex = context.getPartitionIndex();
        esperFunc = new EsperFunc();
        epService = esperFunc.setUpEsper(epService);
    }

    public void cleanup() {
        System.out.println("");
    }

    public void execute(TridentTuple tuple, TridentCollector collector) {
        List<Object> values = tuple.getValues();
        log.info("execute:" + (String) values.get(0));
        LogBean logBean = gson.fromJson((String) values.get(0), LogBean.class);
        epService.getEPRuntime().sendEvent(logBean);
    }

}
