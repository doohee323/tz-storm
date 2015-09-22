package example3.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import example3.tzstorm.bolt.HostnameCountBolt;
import example3.tzstorm.bolt.ReportBolt;
import example3.tzstorm.bolt.SplitLogBolt;
import example3.tzstorm.spout.TestSpout3;

public class TestTopology3 {

    private static final String SPOUT_ID = "testSpout3";
    private static final String SPLIT_BOLT_ID = "split-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_ID = "TestTopology3";

    public static void main(String[] args) throws Exception {
        TestSpout3 spout = new TestSpout3();
        SplitLogBolt splitBolt = new SplitLogBolt();
        HostnameCountBolt countBolt = new HostnameCountBolt();
        ReportBolt reportBolt = new ReportBolt();

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(SPOUT_ID, spout);
        builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("hostname"));
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

        Config cfg = new Config();

        String runType = System.getProperty("runType", "local");
        if (runType.equals("local")) {
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
