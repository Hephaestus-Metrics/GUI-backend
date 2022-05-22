package app.model;

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
                "values=" + values +
                ", isQuery=" + isQuery +
                '}';
    }

}
