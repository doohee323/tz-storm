package example8.tzstorm.bolt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import example8.tzstorm.LogBean;

@SuppressWarnings("rawtypes")
public class EsperFunc {

    private static final long serialVersionUID = 1L;
    static final Logger log = LoggerFactory.getLogger(EsperFunc.class);

    private static final Gson gson = new Gson();

    public EPServiceProvider setUpEsper(EPServiceProvider epService) {
        Configuration configuration = new Configuration();
        configuration.addEventType("Log", LogBean.class.getName());

        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        StringBuffer qeury = new StringBuffer();
        // qeury.append("SELECT COUNT(Log.hostname) AS total, ");
        // qeury.append("Log.hostname AS hostname ");
        // qeury.append("FROM Log.win:time(10 SECOND) ");
        // // qeury.append("WHERE Log.hostname = \"ruleset32.xdn.com\" ");
        // qeury.append("GROUP BY Log.hostname ");
        // qeury.append("OUTPUT SNAPSHOT EVERY 2 SEC ");

        qeury.append("select * from Log t where t.target = \"deploys.test.state\" ");
        EPStatement statement = epService.getEPAdministrator().createEPL(qeury.toString());

        statement.addListener(new UpdateListener() {
            public void update(EventBean[] arg0, EventBean[] arg1) {
                if (arg0 != null) {
                    for (EventBean e : arg0) {
                        JsonArray arry = (JsonArray) new JsonParser().parse(e.get("datapoint").toString());
                        for (int i = 0; i < arry.size(); i++) {
                            if(arry.get(i).getAsJsonArray().get(0).isJsonNull()) {
                                log.error("esper:" + System.currentTimeMillis() + " -> " + e.get("target") + ": " + e.get("timestamp") + ": "
                                        + "null:" + arry.get(i).getAsJsonArray().get(1).getAsString());
                            } else {
                                log.error("esper:" + System.currentTimeMillis() + " -> " + e.get("target") + ": " + e.get("timestamp") + ": "
                                        + arry.get(i).getAsJsonArray().get(0).getAsString() + ":" + arry.get(i).getAsJsonArray());
                            }
                        }
                    }
                }
            }
        });
        return epService;
    }

}
