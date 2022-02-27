package org.example.sdj.advance.support;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.HashMap;
import java.util.Map;

public class ResultRowLine {
    //table,column,value
    private Table<String, String, Object> rowLine = TreeBasedTable.create();

    public Object put(String table, String column, Object value) {
        if (value != null) {
            return rowLine.put(table.toLowerCase(), column, value);
        }
        return null;
    }

    public Map<String, Object> getTableVals(String table) {
        String lowerTable = table.toLowerCase();
        return rowLine.containsRow(lowerTable) ? rowLine.row(lowerTable) : new HashMap<>();
    }
    public boolean conatinsTable(String table){
        return rowLine.containsRow(table.toLowerCase());
    }

    public Object getVal(String table, String column) {
        return rowLine.get(table, column);
    }
}
