package app.services;

import app.model.Filters;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class QueryBuilderServiceTest {
    private static final String KEY_1 = "FILTER_1";
    private static final String VALUE_1 = "VALUE_1";

    private static final String KEY_2 = "FILTER_2";
    private static final String VALUE_2 = "VALUE_2";

    @Test
    void noFiltersTest() {
        //given
        QueryBuilderService queryBuilderService = new QueryBuilderService();

        //when
        String query = queryBuilderService.filtersToQuery(null);

        //then
        assertNull(query);
    }

    @Test
    void emptyFiltersTest() {
        //given
        QueryBuilderService queryBuilderService = new QueryBuilderService();
        Filters filters = new Filters();
        filters.setValues(Collections.emptyMap());

        //when
        String query = queryBuilderService.filtersToQuery(filters);

        //then
        assertNull(query);
    }

    @Test
    void buildQueryFromFiltersTest() {
        //given
        QueryBuilderService queryBuilderService = new QueryBuilderService();
        Filters filters = new Filters();
        filters.setValues(new HashMap<>() {{
            put(KEY_1, VALUE_1);
            put(KEY_2, VALUE_2);
        }});

        //when
        String query = queryBuilderService.filtersToQuery(filters);

        //then
        assertTrue(isValidJson(query));
    }

    private boolean isValidJson(String query) {
        try {
            new JSONObject(query);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
