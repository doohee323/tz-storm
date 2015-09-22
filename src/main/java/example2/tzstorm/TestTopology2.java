package example2.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import example2.tzstorm.bolt.TestBolt2;
import example2.tzstorm.spout.TestSpout2;

public class TestTopology2 {

    private static final String SPOUT_ID = "TestSpout2";
    private static final String BOLT_ID = "TestBolt2";
    private static final String TOPOLOGY_ID = "TestTopology2";

    public static void main(String args[]) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(SPOUT_ID, new TestSpout2(), 2);
        builder.setBolt(BOLT_ID, new TestBolt2(), 2).setNumTasks(4).shuffleGrouping(SPOUT_ID);

        Config conf = new Config();
        String runType = System.getProperty("runType", "local");
        if (runType.equals("local")) {
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
