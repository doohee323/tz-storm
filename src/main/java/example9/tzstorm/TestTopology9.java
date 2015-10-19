package example9.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.utils.Utils;
import example9.tzstorm.bolt.EsperBolt;
import example9.tzstorm.spout.TestSpout9;

public class TestTopology9 {

	private static final String SPOUT_ID = "testSpout9";
	private static final String SPLIT_BOLT_ID = "split-bolt";
	private static final String TOPOLOGY_ID = "TestTopology9";

	public static void main(String args[]) {
		EsperBolt esperBolt = new EsperBolt.Builder().inputs().aliasComponent(SPOUT_ID).toEventType("Log").outputs()
				.outputs().onDefaultStream().emit("target", "timestamp", "datapoint").statements()
				.add("select * from Log where target = \"deploys.test.state\"").build();

		BaseRichSpout spout = new TestSpout9();
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout(SPOUT_ID, spout);
		builder.setBolt(SPLIT_BOLT_ID, esperBolt).shuffleGrouping(SPOUT_ID);

		Config cfg = new Config();
		String runType = System.getProperty("runType", "local");
		if (runType.equals("local")) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
			Utils.sleep(10000000);
			cluster.killTopology(TOPOLOGY_ID);
			cluster.shutdown();
		} else if (runType.equals("storm")) {
			try {
				StormSubmitter.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
			} catch (AlreadyAliveException ae) {
				System.out.println(ae);
			} catch (InvalidTopologyException ie) {
				System.out.println(ie);
			}
		}
	}
}
