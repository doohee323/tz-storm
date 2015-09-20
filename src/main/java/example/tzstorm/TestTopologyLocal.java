package example.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import example.tzstorm.bolt.TestBolt;
import example.tzstorm.spout.TestSpout;

public class TestTopologyLocal {
    public static void main(String args[]) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("TestSpout", new TestSpout(), 2);
        builder.setBolt("TestBolt", new TestBolt(), 4).shuffleGrouping("TestSpout");

        Config conf = new Config();
        conf.setDebug(true);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("TestTopologyLocal", conf, builder.createTopology());
        Utils.sleep(10000);
        cluster.killTopology("TestTopologyLocal");
        cluster.shutdown();
    }

}
