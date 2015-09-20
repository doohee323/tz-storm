package example.tzstorm.bolt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class TestBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TestBolt.class);

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String value = tuple.getStringByField("say");
        log.info("Tuple value is" + value);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        System.out.println("");
    }

}
