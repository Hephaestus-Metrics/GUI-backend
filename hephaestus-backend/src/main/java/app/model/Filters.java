package app.model;

import net.minidev.json.JSONObject;

import java.util.Map;

public class Filters {

    private Map<String, String> values;

    private boolean isQuery;

    public Filters (){
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    // isQuery is prettier but complicates serialization
    public boolean getIsQuery() {
        return isQuery;
    }

    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    @Override
    public String toString() {
        return "Filters{" +
                "values=" + new JSONObject(values) +
                ", isQuery=" + isQuery +
                '}';
    }

    public String toSJSONString() {
        return "{" +
                "\"values\":" + new JSONObject(values) +
                ",\"isQuery\":" + isQuery +
                '}';
    }

}
