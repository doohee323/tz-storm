package example3.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import example3.tzstorm.spout.TestSpout;
import example3.tzstorm.bolt.ReportBolt;
import example3.tzstorm.bolt.SplitSentenceBolt;
import example3.tzstorm.bolt.WordCountBolt;

public class TestTopology3 {

    private static final String SPOUT_ID = "testSpout3";
    private static final String SPLIT_BOLT_ID = "split-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_ID = "TestTopology3";

    public static void main(String[] args) throws Exception {
        TestSpout spout = new TestSpout();
        SplitSentenceBolt splitBolt = new SplitSentenceBolt();
        WordCountBolt countBolt = new WordCountBolt();
        ReportBolt reportBolt = new ReportBolt();

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(SPOUT_ID, spout);
        builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

        Config cfg = new Config();

        boolean localMode = Boolean.parseBoolean(System.getProperty("local", "false"));
        if (localMode) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
            Utils.sleep(10 * 1000);
            cluster.killTopology(TOPOLOGY_ID);
            cluster.shutdown();
        } else {
            StormSubmitter.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
        }
    }

}
