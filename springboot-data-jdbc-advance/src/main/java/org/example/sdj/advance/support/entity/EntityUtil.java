package org.example.sdj.advance.support.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.sdj.advance.support.PersistentEntityCache;
import org.example.sdj.advance.support.ResultRowLine;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EntityUtil {

    static Map<String, Method> PROPERTY_METHOD = new ConcurrentHashMap<>();
    static Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    static String PK_SIGN = "__pk__";

    public static List<Class<? extends BaseEntity>> getRefEntityClass(Class domainType) {
        List<Class<? extends BaseEntity>> classes = new ArrayList<>();
        for (Field field : getAllFields(domainType)) {
            Class clz = getActualType(field);
            if (isEntityType(clz)) {
                classes.add(clz);
            }
        }
        return classes;
    }

    public static Method getClassMethod(Class domainType, String methodPrefix, Class refClass) throws NoSuchMethodException {
        String cachKey = domainType + "." + methodPrefix + refClass.getName();
        if (!PROPERTY_METHOD.containsKey(cachKey)) {
            Method method = Arrays.stream(domainType.getMethods())
                    .filter(m -> {
                        if ("get".equals(methodPrefix)) {
                            return getActualReturnType(m).equals(refClass);
                        } else if ("set".equals(methodPrefix)) {
                            return containsActualParameterType(m, refClass);
                        }
                        return false;
                    }).findAny().orElseThrow(() -> new NoSuchMethodException());
            PROPERTY_METHOD.put(cachKey, method);
        }
        return PROPERTY_METHOD.get(cachKey);
    }

    private static Optional<Field> findField(Class srcClass, Class refClass) {
        return getAllFields(srcClass).stream().filter(f -> refClass.equals(getActualType(f))).findFirst();
    }

    public static Method getClassMethod(Class domainType, String methodPrefix, String property) throws NoSuchMethodException {
        String cachKey = domainType + "." + methodPrefix + property;
        if (!PROPERTY_METHOD.containsKey(cachKey)) {
            Method method = getAllMethods(domainType).stream()
                    .filter(m -> m.getName().startsWith(methodPrefix) && equalsAnyIgnoreCase(m.getName().substring(3), property.replace("_", "")))
                    .findAny().orElseThrow(() -> new NoSuchMethodException());
            PROPERTY_METHOD.put(cachKey, method);
        }
        return PROPERTY_METHOD.get(cachKey);
    }

    private static boolean equalsAnyIgnoreCase(String src, String... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            for (String target : searchStrings) {
                if (src.equalsIgnoreCase(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setEnityRefValue(Object mainObj, Class refClass, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class srcClass = mainObj.getClass();
        Optional<Field> fieldOptional = findField(srcClass, refClass);
        if (!fieldOptional.isPresent()) {
            log.error("setEnityRefValue domainType {}.{} not exist", srcClass, refClass);
            return;
        }

        if (isCollectionLike(fieldOptional.get())) {
            Method getClassMethod = getClassMethod(srcClass, "get", refClass);
            Collection collection = (Collection) getClassMethod.invoke(mainObj);
            if (collection == null) {
                collection = new ArrayList();
                Method setCollection = getClassMethod(srcClass, "set", refClass);
                setCollection.invoke(mainObj, collection);
            }
            collection.add(value);
        } else {
            Method setClassMethod = getClassMethod(srcClass, "set", refClass);
            ReflectionUtils.makeAccessible(setClassMethod);
            ReflectionUtils.invokeMethod(setClassMethod, mainObj, value);
        }
    }


    private static boolean isCollectionLike(Field field) {
        return field.getType().isArray() || Collection.class.isAssignableFrom(field.getType());
    }

    private static Class getActualType(Field f) {
        if (isCollectionLike(f)) {
            ParameterizedType pt = (ParameterizedType) f.getGenericType();
            Class clz = (Class<?>) pt.getActualTypeArguments()[0];
            return clz;
        } else {
            return f.getType();
        }
    }

    private static Class getActualReturnType(Method method) {
        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
            Class clz = (Class<?>) pt.getActualTypeArguments()[0];
            return clz;
        } else {
            return method.getReturnType();
        }
    }

    private static boolean containsActualParameterType(Method method, Class refClass) {
        for (Type type : method.getGenericParameterTypes()) {
            if (type.equals(refClass)) {
                return true;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                Class clz = (Class<?>) pt.getActualTypeArguments()[0];
                return clz.equals(refClass);
            }
        }
        return false;
    }


    public static String getRefKeyName(Class domainType, Class propertyClass) throws NoSuchFieldException {
        String cachKey = domainType + propertyClass.getSimpleName();
        if (!FIELD_CACHE.containsKey(cachKey)) {
            Optional<Field> fieldOptional = getAllFields(domainType).stream()
                    .filter(f -> propertyClass.equals(getActualType(f))).findFirst();
            if (!fieldOptional.isPresent()) {
                throw new NoSuchFieldException(String.format("domainType %s property %s not found", domainType, propertyClass.getSimpleName()));
            }
            FIELD_CACHE.put(cachKey, fieldOptional.get());
        }
        Field field = FIELD_CACHE.get(cachKey);
        Column column = field.getAnnotation(Column.class);
        String propName = column.value();
        return propName;
    }

    private static boolean isEntityType(Class clz) {
        return BaseEntity.class.isAssignableFrom(clz);
    }

    public static <T> Object getPKID(T t) {
        try {
            return getPKMethod(t.getClass(), "get").invoke(t);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error(String.format("getPk err domainType=%s,err=%s", t.getClass(), e.getMessage()));
        }
        return null;
    }

    public static <T> T setPKID(T t, Object pk) {
        try {
            RelationalPersistentEntity entity = PersistentEntityCache.getEntity(t.getClass()).get();
            PersistentPropertyAccessor<T> accessor = entity.getPropertyAccessor(t);
            PersistentPropertyAccessor<T> propertyAccessor = new ConvertingPropertyAccessor<>(accessor, new DefaultConversionService());
            entity.doWithAll(property -> {
                if (property.isIdProperty()) {
                    propertyAccessor.setProperty(property, pk);
                }
            });
            return t;
        } catch (Exception e) {
            log.error(String.format("setPKID err domainType=%s,err=%s", t.getClass(), e.getMessage()));
        }
        return t;
    }

    public static void setPropertyValue(Object bean, Field field, Object value, ConversionService conversionService) throws NoSuchMethodException {
        Class domainType = bean.getClass();
        RelationalPersistentEntity entity = PersistentEntityCache.getEntity(domainType).get();
        PersistentProperty property = entity.getPersistentProperty(field.getName());
        if (property != null) {
            ConvertingPropertyAccessor propertyAccessor = new ConvertingPropertyAccessor<>(entity.getPropertyAccessor(bean), conversionService);
            propertyAccessor.setProperty(property, value);
        } else {
            Method setterMethod = getClassMethod(domainType, "set", field.getName());
            Object convertValue = convertIfNecessary(value, field.getType(), conversionService);
            ReflectionUtils.makeAccessible(setterMethod);
            ReflectionUtils.invokeMethod(setterMethod, bean, convertValue);
        }
    }

    private static <T> T convertIfNecessary(@Nullable Object source, Class<T> type, ConversionService conversionService) {
        return (T) (source == null ? null
                : type.isAssignableFrom(source.getClass()) ? source : conversionService.convert(source, type));
    }

    public static String getPKColumn(Class domainType) {
        String cachKey = domainType + "." + PK_SIGN;

        if (!FIELD_CACHE.containsKey(cachKey)) {
            Optional<Field> fieldOptional = getAllFields(domainType).stream()
                    .filter(f -> f.getAnnotation(Id.class) != null).findFirst();
            if (!fieldOptional.isPresent()) {
                log.error(cachKey + " not found !!");
                return null;
            }
            FIELD_CACHE.put(cachKey, fieldOptional.get());
        }
        Field field = FIELD_CACHE.get(cachKey);
        Column column = field.getAnnotation(Column.class);
        String propName = column.value();
        return propName;
    }

    public static List<Field> getAllFields(Class domainType) {
        List<Field> list = new ArrayList<>();
        ReflectionUtils.doWithFields(domainType, field -> {
            list.add(field);
        });
        return list;
    }

    public static List<Method> getAllMethods(Class domainType) {
        List<Method> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(domainType, method -> {
            methods.add(method);
        });
        return methods;
    }

    private static Method getPKMethod(Class domainType, String prefix) throws NoSuchMethodException {
        String cacheKey = domainType + "." + prefix + PK_SIGN;
        if (!PROPERTY_METHOD.containsKey(domainType)) {
            PersistentProperty idProperty = PersistentEntityCache.getEntity(domainType).get().getIdProperty();
            Method method = getClassMethod(domainType, prefix, idProperty.getName());
            PROPERTY_METHOD.put(cacheKey, method);
        }
        return PROPERTY_METHOD.get(cacheKey);
    }

    public static Object getPKID(Class domainType, ResultRowLine resultRowLine) {
        try {
            Table table = (Table) domainType.getAnnotation(Table.class);
            PersistentProperty idProperty = PersistentEntityCache.getEntity(domainType).get().getIdProperty();
            Column idColumn = idProperty.getField().getAnnotation(Column.class);
            return resultRowLine.getTableVals(table.value()).get(idColumn.value());
        } catch (Exception e) {
            log.error(String.format("getPKID err domainType=%s,err=%s", domainType, e.getMessage()));
        }
        return null;
    }

    public static <T> Object getProperty(@NonNull T t, @NonNull String property) {
        String methodName = null;
        Class domainType = t.getClass();
        try {
            Method method = getClassMethod(domainType, "get", property);
            methodName = method.getName();
            return method.invoke(t);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            log.error(String.format("getProperty err domainType=%s,methodName=%s,err=%s", domainType, methodName, e.getMessage()));
        }
        return null;
    }

    public static <T> Object getProperty(T t, Field field) {
        String methodName = null;
        try {
            Column column = field.getAnnotation(Column.class);
            Method method = getClassMethod(t.getClass(), "get", column.value());
            return method.invoke(t);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error(String.format("getProperty err domainType=%s,methodName=%s,err=%s", t.getClass(), methodName, e.getMessage()));
        }
        return null;
    }

    public static boolean isNotEmptyObj(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Integer || obj instanceof Long || obj instanceof Short) {
            return !"0".equals(obj + "");
        } else if (obj instanceof Double || obj instanceof Float || obj instanceof BigDecimal) {
            return !".".equals(obj.toString().replace("0", ""));
        } else if (obj instanceof String) {
            return StringUtils.isNotBlank(obj.toString());
        }
        return true;
    }

    public static String tableName(@NonNull Class domainType) {
        Table table = (Table) domainType.getAnnotation(Table.class);
        return table.value();
    }

}
