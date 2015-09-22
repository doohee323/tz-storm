package example4.tzstorm;

import java.util.Map;

import org.json.simple.JSONValue;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.Nimbus.Client;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;
import example4.tzstorm.bolt.HostnameCountBolt;
import example4.tzstorm.bolt.ReportBolt;
import example4.tzstorm.bolt.SplitLogBolt;

public class TestTopology4 {

    private static final String zkHostPort = "localhost:2181";
    private static final String stormNimbusHost = "127.0.0.1";
    private static final String TOPIC = "logs";

    private static final String SPOUT_ID = "testSpout4";
    private static final String SPLIT_BOLT_ID = "split-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_ID = "TestTopology4";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String runType = System.getProperty("runType", "local");
        BaseRichSpout spout = null;
        spout = (KafkaSpout) buildKafkaSpout();
        SplitLogBolt splitBolt = new SplitLogBolt();
        HostnameCountBolt countBolt = new HostnameCountBolt();
        ReportBolt reportBolt = new ReportBolt();

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(SPOUT_ID, spout);
        builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("hostname"));
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

        Config cfg = new Config();

        if (runType.equals("local")) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
            Utils.sleep(10 * 1000);
            cluster.killTopology(TOPOLOGY_ID);
            cluster.shutdown();
        } else if (runType.equals("storm")) {
            StormSubmitter.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
        } else if (runType.equals("kafka")) {
            Map<String, String> storm_conf = Utils.readStormConfig();
            storm_conf.put("nimbus.host", stormNimbusHost);
            Client client = NimbusClient.getConfiguredClient(storm_conf).getClient();
            String inputJar = "/Users/dhong/git_java/kstorm/target/kstorm-1.0-SNAPSHOT-jar-with-dependencies.jar";
            StormSubmitter.submitJar(storm_conf, inputJar);
            String jsonConf = JSONValue.toJSONString(storm_conf);
            client.submitTopology(TOPOLOGY_ID, inputJar, jsonConf, builder.createTopology());
        }
    }

    private static KafkaSpout buildKafkaSpout() {
        String zkRoot = "/kafka-log-spout";
        String zkSpoutId = "log-spout";
        ZkHosts zkHosts = new ZkHosts(zkHostPort);

        SpoutConfig spoutCfg = new SpoutConfig(zkHosts, TOPIC, zkRoot, zkSpoutId);
        KafkaSpout kafkaSpout = new KafkaSpout(spoutCfg);
        return kafkaSpout;
    }

}
