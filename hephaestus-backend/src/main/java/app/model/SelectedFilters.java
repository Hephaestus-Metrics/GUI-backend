package app.model;

import app.services.QueryBuilderService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SelectedFilters implements SelectedQuery {

    private final Map<String, String> filters;

    private final String query;

    @JsonCreator
    public SelectedFilters(@JsonProperty("filters") Map<String, String> filters) {
        this.filters = filters;
        this.query = QueryBuilderService.INSTANCE.filtersToQuery(filters);
    }

    @Override
    @JsonIgnore
    public String getTag() {
        return "__#AUTOGENERATED__" + filters.toString();
    }

    @Override
    @JsonIgnore
    public String getQueryString() {
        return query;
    }

    public Map<String, String> getFilters() {
        return filters;
    }
}
