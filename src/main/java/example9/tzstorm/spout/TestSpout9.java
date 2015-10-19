package example9.tzstorm.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import example3.tzstorm.spout.TestSpout3;
import example8.tzstorm.graphite.GraphiteClient;
import example9.tzstorm.LogBean;

public class TestSpout9 extends BaseRichSpout {

	private static final long serialVersionUID = -462261441867084611L;
	private static final Log log = LogFactory.getLog(TestSpout3.class);

	private SpoutOutputCollector collector;
	private List<String> logData = new ArrayList<String>();
	private static final Gson gson = new Gson();

	public void open(@SuppressWarnings("rawtypes") Map cfg, TopologyContext topology,
			SpoutOutputCollector outCollector) {
		collector = outCollector;
		// log.debug("Emitter.emitBatch({}, {}, collector)", tx,
		// coordinatorMeta);
		GraphiteClient client = new GraphiteClient();
		logData = client.getData();
		log.debug("");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("target", "timestamp", "datapoint"));
	}

	public void nextTuple() {
		for (String str : logData) {
			log.info(str);
			LogBean logBean = gson.fromJson(str, LogBean.class);
			ArrayList<Object> tuple = new ArrayList<Object>(3);
			tuple.add(logBean.getTarget());
			tuple.add(logBean.getTimestamp());
			tuple.add(logBean.getDatapoint());
			collector.emit(tuple);
		}
	}
}