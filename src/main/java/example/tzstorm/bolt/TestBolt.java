package example.tzstorm.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class TestBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String value = tuple.getStringByField("say");
        System.out.println("Tuple value is" + value);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        System.out.println("");
    }

}
