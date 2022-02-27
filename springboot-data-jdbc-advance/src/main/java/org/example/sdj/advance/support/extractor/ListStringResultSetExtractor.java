package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListStringResultSetExtractor implements ResultSetExtractor<List<String>> {

    @Override
    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        return list;
    }
}
