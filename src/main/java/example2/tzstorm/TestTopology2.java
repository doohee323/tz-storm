package example2.tzstorm;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import example2.tzstorm.bolt.TestBolt2;
import example2.tzstorm.spout.TestSpout2;

public class TestTopology2 {

	public static void main(String args[]) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("TestSpout2", new TestSpout2(), 2);
		builder.setBolt("TestBolt2", new TestBolt2(), 2).setNumTasks(4)
				.shuffleGrouping("TestSpout2");

		Config conf = new Config();
		conf.setNumWorkers(5);
		try {
			StormSubmitter.submitTopology(args[0], conf,
					builder.createTopology());
		} catch (AlreadyAliveException ae) {
			System.out.println(ae);
		} catch (InvalidTopologyException ie) {
			System.out.println(ie);
		}
	}

}
