package example2.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import example2.tzstorm.bolt.TestBolt2;
import example2.tzstorm.spout.TestSpout2;

public class TestTopologyLocal2 {
	public static void main(String args[]) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("TestSpout2", new TestSpout2(), 2);
		builder.setBolt("TestBolt2", new TestBolt2(), 4).shuffleGrouping(
				"TestSpout2");

		Config conf = new Config();
		conf.setDebug(true);
		LocalCluster cluster = new LocalCluster();

		cluster.submitTopology("TestTopologyLocal2", conf,
				builder.createTopology());
		Utils.sleep(10000);
		cluster.killTopology("TestTopologyLocal2");
		cluster.shutdown();
	}

}
