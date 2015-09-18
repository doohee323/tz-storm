package example.tzstorm;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class TestTopology {
    public static void main(String args[]) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("TestSpout", new TestSpout(), 2);
        builder.setBolt("TestBolt", new TestBolt(), 2)
        .setNumTasks(4).shuffleGrouping("TestSpout");

        Config conf = new Config();
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
