package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapIntegetAndIntegerListResultSetExtractor implements ResultSetExtractor<Map<Integer, List<Integer>>> {
    @Override
    public Map<Integer, List<Integer>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<Integer>> map = new HashMap<>();
        while (rs.next()) {
            Integer key = rs.getInt(1);
            Integer val = rs.getInt(2);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(val);
        }
        return map;
    }
}
