package example.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import example.tzstorm.bolt.TestBolt;
import example.tzstorm.spout.TestSpout;

public class TestTopology {

    private static final String SPOUT_ID = "TestSpout";
    private static final String BOLT_ID = "TestBolt";
    private static final String TOPOLOGY_ID = "TestTopology";

    public static void main(String args[]) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(SPOUT_ID, new TestSpout(), 2);
        builder.setBolt(BOLT_ID, new TestBolt(), 2).setNumTasks(4).shuffleGrouping(SPOUT_ID);

        boolean localMode = Boolean.parseBoolean(System.getProperty("local", "false"));
        Config conf = new Config();
        if (localMode) {
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();

            cluster.submitTopology(TOPOLOGY_ID, conf, builder.createTopology());
            Utils.sleep(10000);
            cluster.killTopology(TOPOLOGY_ID);
            cluster.shutdown();
        } else {
            conf.setNumWorkers(5);
            try {
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (AlreadyAliveException ae) {
                System.out.println(ae);
            } catch (InvalidTopologyException ie) {
                System.out.println(ie);
            }
        }
    }
}
