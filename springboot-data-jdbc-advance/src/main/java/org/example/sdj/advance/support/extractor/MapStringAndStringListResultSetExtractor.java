package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapStringAndStringListResultSetExtractor implements ResultSetExtractor<Map<String, List<String>>> {
    @Override
    public Map<String, List<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, List<String>> map = new HashMap<>();
        while (rs.next()) {
            String key = rs.getString(1);
            String val = rs.getString(2);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(val);
        }
        return map;
    }
}
