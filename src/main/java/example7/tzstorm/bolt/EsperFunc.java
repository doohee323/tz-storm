package example7.tzstorm.bolt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;

import example7.tzstorm.LogBean;

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

        qeury.append("select * from Log t where t.hostname = \"ruleset33.xdn.com\" ");
        EPStatement statement = epService.getEPAdministrator().createEPL(qeury.toString());

        statement.addListener(new UpdateListener() {
            public void update(EventBean[] arg0, EventBean[] arg1) {
                if (arg0 != null) {
                    for (EventBean e : arg0) {
                        // log.error("log count for each " + e.get("hostname")
                        // + ": " + e.get("total"));
                        log.error("esper:" + System.currentTimeMillis() + " -> " + e.get("timestamp") + ": "
                                + e.get("hostname"));
                    }
                }
            }
        });
        return epService;
    }

}
