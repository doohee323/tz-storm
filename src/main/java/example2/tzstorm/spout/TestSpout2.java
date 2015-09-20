package example2.tzstorm.spout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import example2.tzstorm.MessageBean;

public class TestSpout2 extends BaseRichSpout {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TestSpout2.class);

	private SpoutOutputCollector collector;
	private static String LOG_FILENAME = "data/a.log";
	private Set<Object> logData = new HashSet<Object>();

	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		this.prepareData();
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void prepareData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(LOG_FILENAME));
			String oneLine = br.readLine();
			if (oneLine == null) {
				br.close();
				throw new Exception("!!!");
			}

			int totalLines = 0;
			while (oneLine != null) {
				totalLines++;
				logData.add(MessageBean.parse(oneLine));
				oneLine = br.readLine();
			}
			log.info("Lines in ten second log: " + totalLines);
			br.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	/**
	 */
	public void nextTuple() {
		while (!this.logData.isEmpty()) {
			Iterator<Object> itr = this.logData.iterator();
			MessageBean item = (MessageBean) itr.next();
			collector.emit(new Values(item));
			this.logData.remove(item);
		}
	}

}