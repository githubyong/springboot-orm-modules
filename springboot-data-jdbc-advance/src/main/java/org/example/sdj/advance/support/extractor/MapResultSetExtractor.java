package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MapResultSetExtractor implements ResultSetExtractor<Map> {

    @Override
    public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            Map<String, String> map = new HashMap<>();
            ResultSetMetaData meta = rs.getMetaData();
            int col = meta.getColumnCount();
            for (int i = 1; i <= col; i++) {
                map.put(meta.getColumnName(i), rs.getString(i));
            }
            return map;
        }

        return null;
    }
}
