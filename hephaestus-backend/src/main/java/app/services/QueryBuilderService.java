package app.services;

import app.model.Filters;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QueryBuilderService {

    @Nullable
    public String filtersToQuery(Filters filters){
        if (filters == null || filters.getValues().isEmpty()) {
            // query can't be constructed - no filters
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, String> entry : filters.getValues().entrySet()){
            sb.append(entry.getKey());
            sb.append("=\"");
            sb.append(entry.getValue());
            sb.append("\",");
        }
        sb.append("}");
        return sb.toString();
    }

}
