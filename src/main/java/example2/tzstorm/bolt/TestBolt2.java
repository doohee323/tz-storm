package example2.tzstorm.bolt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import example2.tzstorm.MessageBean;

public class TestBolt2 extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	static final Logger log = LoggerFactory.getLogger(TestBolt2.class);

	private OutputCollector collector;
	private EPServiceProvider epService;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.setUpEsper();
	}

	private void setUpEsper() {
		Configuration configuration = new Configuration();
		configuration.addEventType("Log", MessageBean.class.getName());

		epService = EPServiceProviderManager.getDefaultProvider(configuration);
		epService.initialize();

		EPStatement statement = epService
				.getEPAdministrator()
				.createEPL(
						"select count(distinct Log.clientID) as total from Log.win:time(6 second) output snapshot every 2 sec");
		
		statement.addListener(new UpdateListener() {
			public void update(EventBean[] arg0, EventBean[] arg1) {
				if (arg0 != null) {
					for (EventBean e : arg0) {
						log.info("online visitors: " + e.get("total"));
					}
				}
			}

		});
	}

	public void execute(Tuple input) {
		List<Object> values = input.getValues();
		epService.getEPRuntime().sendEvent(values.get(0));
		collector.ack(input);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
