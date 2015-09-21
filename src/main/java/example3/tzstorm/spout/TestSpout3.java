package example3.tzstorm.spout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import example3.tzstorm.spout.TestSpout3;

public class TestSpout3 extends BaseRichSpout {

    private static final long serialVersionUID = -462261441867084611L;
    private static final Log log = LogFactory.getLog(TestSpout3.class);

    private SpoutOutputCollector collector;
    private static String LOG_FILENAME = "data/a.txt";
    private List<Object> logData = new ArrayList<Object>();
    private int idx = 0;

    public void open(@SuppressWarnings("rawtypes") Map cfg, TopologyContext topology, SpoutOutputCollector outCollector) {
        collector = outCollector;
        this.prepareData();
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("Log"));
    }

    public void nextTuple() {
        collector.emit(new Values(this.logData.get(idx)));
        idx++;
        if (idx >= this.logData.size()) {
            idx = 0;
        }
        Utils.sleep(500);
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
                logData.add(oneLine);
                oneLine = br.readLine();
            }
            log.error("Lines in ten second log: " + totalLines);
            br.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}