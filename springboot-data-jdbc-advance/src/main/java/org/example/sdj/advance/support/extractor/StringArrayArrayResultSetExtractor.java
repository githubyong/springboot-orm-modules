package org.example.sdj.advance.support.extractor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringArrayArrayResultSetExtractor implements ResultSetExtractor<String[][]> {
    @Override
    public String[][] extractData(ResultSet rs) throws SQLException, DataAccessException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int colSize = rsMetaData.getColumnCount();
        List<String[]> listResult = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[colSize];
            for (int j = 0; j < colSize; ++j) {
                String temStr = rs.getString(j + 1);
                if (temStr != null) {
                    row[j] = temStr;
                } else {
                    row[j] = StringUtils.EMPTY;
                }
            }
            listResult.add(row);
        }
        return listResult.toArray(new String[0][0]);
    }
}
