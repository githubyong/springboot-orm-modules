package org.example.sdj.advance.support.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * exact muti column result </br>
 * eg: select col1,col2 from table;</br>
 *  return string[col_val1,col_val2]
 */
public class StringArrayResultSetExtractor implements ResultSetExtractor<String[]> {

    @Override
    public String[] extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            List<String> list = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int col = meta.getColumnCount();
            for (int i = 1; i <= col; i++) {
                list.add(rs.getString(i));
            }
            return list.toArray(new String[0]);
        }

        return null;
    }
}
