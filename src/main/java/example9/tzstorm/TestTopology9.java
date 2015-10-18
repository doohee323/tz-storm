package example9.tzstorm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import example9.tzstorm.bolt.EsperBolt;
import example9.tzstorm.bolt.TestBolt9;
import example9.tzstorm.operator.DistinctStateFactory;
import example9.tzstorm.operator.DistinctStateUpdater;
import example9.tzstorm.spout.TestSpout9;
import storm.trident.TridentTopology;

public class TestTopology9 {

    private static final String TOPOLOGY_ID = "TestTopology9";

    public static void main(String args[]) {
        EsperBolt esperBolt =
        	    new EsperBolt.Builder()
        	                 .inputs()
        	                    .aliasComponent("some-spout")
        	                    .withFields("a", "b")
        	                    .ofType(Integer.class)
        	                    .toEventType("Test")
        	                 .outputs()
        	                    .outputs().onDefaultStream().emit("min", "max")
        	                 .statements()
        	                    .add("select max(a) as max, min(b) as min from Test.win:length_batch(4)")
        	                 .build();    	
    	
        TridentTopology topology = new TridentTopology();
        topology.newStream("log", new TestSpout9())
                .partitionPersist(new DistinctStateFactory(), new Fields("logstring"), new DistinctStateUpdater(),
                        new Fields("logstring2")).newValuesStream()
                .each(new Fields("logstring2"), new TestBolt9(), new Fields("logstring3"));
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
