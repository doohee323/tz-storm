package example9.tzstorm.graphite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import example8.tzstorm.LogBean;

public class GraphiteClient {

    private static final Gson gson = new Gson();
    static final Logger log = LoggerFactory.getLogger(GraphiteClient.class);

    public List<String> getData() {
        List<String> logData = new ArrayList<String>();
        try {
            StringBuffer bf = new StringBuffer();
            URL url = new URL(
                    "http://172.30.168.10/render?target=deploys.test.state&from=-5min&rawData=true&format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                bf.append(line);
            }

            JsonArray arry = (JsonArray) new JsonParser().parse(bf.toString());
            for (int i = 0; i < arry.size(); i++) {
                LogBean bean = new LogBean();
                bean.setTarget(arry.get(i).getAsJsonObject().get("target").getAsString());
                bean.setDatapoint(gson.toJson(arry.get(i).getAsJsonObject().get("datapoints")));
                bean.setTimestamp(arry.get(i).getAsJsonObject().get("datapoints").getAsJsonArray().get(0).getAsJsonArray().get(1).getAsString());
                logData.add(gson.toJson(bean));
                log.info(gson.toJson(bean));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logData;
    }

    public static void main(String[] args) {
        try {
            URL url = new URL(
                    "http://172.30.168.10/render?target=deploys.test.state&from=-5min&rawData=true&format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}