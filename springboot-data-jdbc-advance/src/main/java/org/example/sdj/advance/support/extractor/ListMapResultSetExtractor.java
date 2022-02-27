package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapResultSetExtractor implements ResultSetExtractor<List<Map<String,String>>> {
    @Override
    public List<Map<String,String>>extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Map<String,String>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, String> map = new HashMap<>();
            ResultSetMetaData meta = rs.getMetaData();
            int col = meta.getColumnCount();
            for (int i = 1; i <= col; i++) {
                map.put(meta.getColumnName(i), rs.getString(i));
            }
            list.add(map);
        }
        return list;
    }
}
