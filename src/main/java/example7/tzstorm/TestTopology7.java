package example7.tzstorm;

import storm.trident.TridentTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import example7.tzstorm.bolt.TestBolt7;
import example7.tzstorm.operator.DistinctStateFactory;
import example7.tzstorm.operator.DistinctStateUpdater;
import example7.tzstorm.spout.TestSpout7;
import example7.tzstorm.zmq.ZMQServer;


public class TestTopology7 {

    private static final String TOPOLOGY_ID = "TestTopology7";

    public static void main(String args[]) {
        TridentTopology topology = new TridentTopology();
        topology.newStream("log", new TestSpout7())
                .partitionPersist(new DistinctStateFactory(), new Fields("logstring"), new DistinctStateUpdater(),
                        new Fields("logstring2")).newValuesStream()
                .each(new Fields("logstring2"), new TestBolt7(), new Fields("logstring3"));
        StormTopology stormTopology = topology.build();

        Config conf = new Config();
        String runType = System.getProperty("runType", "local");
        if (runType.equals("local")) {
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();

            cluster.submitTopology(TOPOLOGY_ID, conf, stormTopology);
            Utils.sleep(10000000);
            cluster.killTopology(TOPOLOGY_ID);
            cluster.shutdown();
        } else {
            conf.setNumWorkers(5);
            try {
                StormSubmitter.submitTopology(args[0], conf, stormTopology);
            } catch (AlreadyAliveException ae) {
                System.out.println(ae);
            } catch (InvalidTopologyException ie) {
                System.out.println(ie);
            }
        }
    }
}
