package org.example.sdj.advance.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.sdj.advance.support.entity.BaseEntity;
import org.example.sdj.advance.support.entity.CustomField;
import org.example.sdj.advance.support.entity.EntityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jdbc.core.convert.BasicJdbcConverter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ResultReadUtil {

    public static <T extends BaseEntity> T readEntity(ResultRowLine resultRowLine, Class<T> domainType, Map<String, String> propsMapping) {
        RelationalPersistentEntity entity = PersistentEntityCache.getEntity(domainType).get();
        String tableName = entity.getTableName().getReference();
        Map<String, Object> valMap = resultRowLine.getTableVals(tableName.toLowerCase());
        Map<String, String> finalPropsMapping = propsMapping != null ? propsMapping : new HashMap<>();
        ReflectionUtils.doWithFields(domainType, field -> {
            CustomField customField = field.getAnnotation(CustomField.class);
            if (customField != null && StringUtils.isNotBlank(customField.refValue())) {
                finalPropsMapping.put(getColumnName(field), customField.refValue());
            }
        });

        for (Map.Entry<String, String> propEntry : finalPropsMapping.entrySet()) {
            String[] propRef = propEntry.getValue().split("\\.");
            Object refVal = resultRowLine.getVal(propRef[0].toLowerCase(), propRef[1]);
            if (refVal != null) {
                valMap.put(propEntry.getKey(), refVal);
            }
        }
        return readEntity(valMap, domainType);
    }


    public static void extendRow(ResultRowLine rowLine, Object currentLevelEntity, List<Class> usedClass,  Map<String, String> propsMapping) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (currentLevelEntity == null) {
            return;
        }
        List<Class<? extends BaseEntity>> subRefClasses = EntityUtil.getRefEntityClass(currentLevelEntity.getClass());
        for (Class<? extends BaseEntity> refClass : subRefClasses) {
            if (usedClass.contains(refClass)) {
                continue;
            }
            BaseEntity refEntity = ResultReadUtil.readEntity(rowLine, refClass, propsMapping);
            usedClass.add(refClass);
            if (refEntity != null) {
                EntityUtil.setEnityRefValue(currentLevelEntity, refClass, refEntity);
                extendRow(rowLine, refEntity, usedClass, propsMapping);
            }
        }
    }

    private static <T> T readEntity(Map<String, Object> valueMap, Class<T> domainType) {
        if (MapUtils.isEmpty(valueMap)) {
            return null;
        }
        T instanceBean = BeanUtils.instantiateClass(domainType);
        ReflectionUtils.doWithFields(domainType, field -> {
            String column = getColumnName(field);
            Object value = valueMap.get(column);
            if (value != null) {
                try {
                    EntityUtil.setPropertyValue(instanceBean, field, value, getConversionService());
                } catch (Exception e) {
                    log.error("readEntity setPropertyValue err:domainType={},field={},value={}", domainType, field, value);
                }
            }
        });
        return instanceBean;
    }

    public static List<ResultRowLine> readResultRowLines(ResultSet rs) throws SQLException, DataAccessException {
        List<ResultRowLine> resultRowLines = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            ResultRowLine resultRowLine = new ResultRowLine();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                resultRowLine.put(metaData.getTableName(i).toLowerCase(), metaData.getColumnName(i), rs.getObject(i));
                resultRowLine.put(metaData.getTableName(i).toLowerCase(), metaData.getColumnLabel(i), rs.getObject(i));
            }
            resultRowLines.add(resultRowLine);
        }
        return resultRowLines;
    }

    public static ResultRowLine readResultRowLine(ResultSet rs) throws SQLException, DataAccessException {
        ResultSetMetaData metaData = rs.getMetaData();
        ResultRowLine resultRowLine = new ResultRowLine();
        for (int i = 1; i < metaData.getColumnCount(); i++) {
            resultRowLine.put(metaData.getTableName(i).toLowerCase(), metaData.getColumnName(i), rs.getObject(i));
        }
        return resultRowLine;
    }

    private static String getColumnName(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);
        return column != null ? column.value() : field.getName();
    }

    public static ConversionService getConversionService(){
        return SpringContextUtil.getBean(BasicJdbcConverter.class).getConversionService();
    }

}
