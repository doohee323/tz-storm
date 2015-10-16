package example8.tzstorm;

import com.google.gson.Gson;

public class LogBean {

    private static final Gson gson = new Gson();

    public static LogBean parse(String line) {
        return gson.fromJson(line, LogBean.class);
    }

    private String target;
    private String datapoint;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDatapoint() {
        return datapoint;
    }

    public void setDatapoint(String datapoint) {
        this.datapoint = datapoint;
    }

}
