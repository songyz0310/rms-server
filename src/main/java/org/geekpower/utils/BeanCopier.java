package org.geekpower.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.geekpower.common.Tuple;

/**
 * 使用反射加缓存的方式进行对象字段值拷贝，用户对象间的转换，前提条件是要拷贝的字段名相同，类型相同。
 * 支持包装类与基本类型间拷贝；支持对保护和私有字段的拷贝；支持继承字段的拷贝；不拷贝static和final字段
 * 
 * 
 * 
 * @author wujin CreateTime: 2018-05-19 16:06
 */
public class BeanCopier {
    private static Map<String, BeanInfo> beanCache = new ConcurrentHashMap<>();

    public interface Converter {
        Object convert(Object value, Class<?> target);
    }

    /**
     * 拷贝对象同名字段值到目标类，如果目标对象有同名字段但字段类型不匹配将忽略拷贝，原始类型的包装类除外。 注意：对于复杂的对象字段是做的浅拷贝
     * 
     * @param from 源对象实例
     * @param to 目标类，必须提供无参数到构造函数
     * @return 目标类对象实例
     */
    public static <T> T copy(Object from, Class<T> to) {
        if (Objects.isNull(from)) {
            return null;
        }

        T objTo = createInstance(to);
        copy(from, objTo);
        return objTo;
    }

    /**
     * 拷贝对象同名字段值到目标类实例，如果目标对象有同名字段但字段类型不匹配将抛出异常，原始类型的包装类除外 注意：对于复杂的对象字段是做的浅拷贝
     * 
     * @param from 源对象
     * @param to 目标对象
     */
    public static <T> void copy(Object from, T to) {
        if (Objects.isNull(from)) {
            return;
        }

        BeanInfo src = getBeanInfo(from.getClass());
        BeanInfo target = getBeanInfo(to.getClass());

        copy(src, target, from, to);
    }

    /**
     * 将List集合中的每个对象的字段值拷贝到目标类型中的同名字段上
     * 
     * @param from
     * @param to
     * @return
     */
    public static <T> List<T> copyList(List<?> from, Class<T> to) {
        if (Objects.isNull(from) || from.isEmpty()) {
            return new ArrayList<>(0);
        }

        BeanInfo src = getBeanInfo(from.get(0).getClass());
        BeanInfo target = getBeanInfo(to);

        List<T> ret = new ArrayList<>(from.size());
        for (Object objFrom : from) {
            T objTo = createInstance(to);
            copy(src, target, objFrom, objTo);
            ret.add(objTo);
        }

        return ret;
    }

    /**
     * 拷贝Map中Key与目标类字段名相同的值，Map中基本类型和包装类型的值直接拷贝，String类型的值尝试进行基本类型转换
     * 
     * @param from
     * @param to
     * @return
     */
    public static <T> T map2Object(Map<String, Object> from, Class<T> to) {
        if (Objects.isNull(from) || from.isEmpty()) {
            return null;
        }

        T objTo = createInstance(to);
        BeanInfo target = getBeanInfo(to);

        from.forEach((key, value) -> {
            FieldInfo targetField = target.getField(key);
            // 跳过没有匹配的字段
            if (Objects.isNull(targetField)) {
                return;
            }

            try {
                if (targetField.isPrimitive || targetField.isBoxed) {
                    if (value.getClass() == String.class) {
                        targetField.setPrimitiveValue(objTo, (String) value);
                    }
                    else if (Objects.nonNull(isBoxed(value.getClass()))) {
                        targetField.setPrimitiveValue(objTo, String.valueOf(value));
                    }
                }
                else {
                    targetField.setValue(objTo, value);
                }
            }
            catch (Exception exp) {
                throw new BaseException(BaseError.SET_VALUE_FAILED.getCode(), exp,
                        BaseError.SET_VALUE_FAILED.getDescription(), target.getBeanName(), targetField.getName());
            }
        });

        return objTo;
    }

    public static <T> Map<String, Object> object2Map(T from) {
        if (Objects.isNull(from)) {
            return null;
        }

        Map<String, Object> ret = new HashMap<>();
        BeanInfo src = getBeanInfo(from.getClass());
        src.fields.forEach(info -> {
            try {
                ret.put(info.field.getName(), info.field.get(from));
            }
            catch (Exception exp) {
                throw new BaseException(BaseError.OBJ_CREATE_FAILED.getCode(), exp,
                        BaseError.OBJ_CREATE_FAILED.getDescription(), info.getName());
            }
        });
        return ret;
    }

    private static <T> T createInstance(Class<T> clz) {
        try {
            return clz.getDeclaredConstructor().newInstance();
        }
        catch (Exception exp) {
            throw new BaseException(BaseError.OBJ_CREATE_FAILED.getCode(), exp,
                    BaseError.OBJ_CREATE_FAILED.getDescription(), clz.getName());
        }
    }

    private static void copy(BeanInfo from, BeanInfo to, Object objFrom, Object objTo) {
        for (FieldInfo srcField : from.getFields()) {
            try {
                FieldInfo targetField = to.getField(srcField.getName());
                if (Objects.isNull(targetField))
                    continue;

                int mod = targetField.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod))
                    continue;

                if (targetField.isSameType(srcField)) {
                    Object v = srcField.getValue(objFrom);
                    if (Objects.isNull(v)) {
                        // 调用set方法设置Null值，如果没有set方法则忽略null
                        if (Objects.nonNull(targetField.setMethod)) {
                            targetField.setMethod.setAccessible(true);
                            targetField.setMethod.invoke(objTo, v);
                        }
                    }
                    else {
                        targetField.setValue(objTo, v);
                    }
                }
            }
            catch (Exception exp) {
                throw new BaseException(BaseError.SET_VALUE_FAILED.getCode(), exp,
                        BaseError.SET_VALUE_FAILED.getDescription(), to.getBeanName(), srcField.getName());
            }
        }
    }

    private static BeanInfo getBeanInfo(Class<?> clz) {
        BeanInfo beanInfo = beanCache.get(clz.getName());
        if (Objects.isNull(beanInfo)) {
            beanInfo = new BeanInfo(clz);
            beanCache.putIfAbsent(clz.getName(), beanInfo);
        }

        return beanInfo;
    }

    private static class BeanInfo {
        String className;
        List<FieldInfo> fields;
        Map<String, FieldInfo> fieldMap;

        BeanInfo(Class<?> clz) {
            this.className = clz.getName();
            fieldMap = new HashMap<>();
            fields = new ArrayList<>(16);

            loadFields(clz, fields, true);
            fields.forEach(f -> fieldMap.put(f.getName(), f));
        }

        String getBeanName() {
            return className;
        }

        FieldInfo getField(String name) {
            return fieldMap.get(name);
        }

        List<FieldInfo> getFields() {
            return fields;
        }

        /**
         * 递归获取类和父类的字段列表，父类只取有public get方法的字段
         * 
         * @param clz
         * @param fieldList
         * @param all
         */
        void loadFields(Class<?> clz, List<FieldInfo> fieldList, boolean all) {
            // 获取public的get方法列表和set方法列表
            Map<String, Method> getMethods = new HashMap<>();
            if (!all) {
                Method[] ms = clz.getDeclaredMethods();
                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().startsWith("get") || ms[i].getName().startsWith("is")) {
                        getMethods.put(ms[i].getName().toLowerCase(), ms[i]);
                    }
                }
            }

            Field[] fs = clz.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Method method;
                try {
                    method = clz.getDeclaredMethod("set" + CommonUtils.toCapitalLetters(fs[i].getName()),
                            fs[i].getType());
                }
                catch (NoSuchMethodException e) {
                    method = null;
                }

                if (all) {
                    fieldList.add(new FieldInfo(fs[i], method));
                }
                else {
                    String fname = fs[i].getName().toLowerCase();
                    if (getMethods.containsKey("get" + fname) || getMethods.containsKey("is" + fname)) {
                        fieldList.add(new FieldInfo(fs[i], method));
                    }
                }
            }

            if (Objects.nonNull(clz.getSuperclass()) && clz.getSuperclass() != Object.class) {
                loadFields(clz.getSuperclass(), fieldList, false);
            }
            else {
                getMethods.clear();
                return;
            }
        }
    }

    private static class FieldInfo {
        Field field;
        boolean isBoxed;
        boolean isPrimitive;
        Tuple.Pair<Class<?>, Class<?>> boxedType;
        Method setMethod;

        FieldInfo(Field field, Method setMethod) {
            this.field = field;
            this.boxedType = isBoxed(field.getType());
            this.isBoxed = this.boxedType == null ? false : true;
            this.isPrimitive = field.getType().isPrimitive();
            this.setMethod = setMethod;
            this.field.setAccessible(true);
        }

        String getName() {
            return field.getName();
        }

        /**
         * 修饰符标示：public、private、static、final、synchronized、abstract 用 Modifier.toString 获取名称
         * 
         * @return
         */
        int getModifiers() {
            return field.getModifiers();
        }

        Object getValue(Object obj) throws IllegalArgumentException, IllegalAccessException {
            return field.get(obj);
        }

        void setValue(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
            field.set(obj, value);
        }

        boolean isSameType(FieldInfo other) {
            if (isPrimitive) {
                if (other.isPrimitive) {
                    return other.field.getType() == field.getType();
                }
                else if (other.isBoxed) {
                    return other.boxedType.getSecond() == field.getType();
                }
            }
            else if (isBoxed) {
                if (other.isPrimitive) {
                    return other.field.getType() == boxedType.getSecond();
                }
                else if (other.isBoxed) {
                    return other.field.getType() == field.getType();
                }
            }

            return other.field.getType() == field.getType();
        }

        void setPrimitiveValue(Object obj, String value) throws IllegalArgumentException, IllegalAccessException {
            if (Objects.isNull(value))
                return;

            if (field.getType() == int.class || field.getType() == Integer.class) {
                field.set(obj, new BigDecimal(value).intValue());
            }
            else if (field.getType() == long.class || field.getType() == Long.class) {
                field.set(obj, new BigDecimal(value).longValue());
            }
            else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.set(obj, Boolean.parseBoolean(value));
            }
            else if (field.getType() == double.class || field.getType() == Double.class) {
                field.set(obj, new BigDecimal(value).doubleValue());
            }
            if (field.getType() == byte.class || field.getType() == Byte.class) {
                field.set(obj, new BigDecimal(value).byteValue());
            }
            else if (field.getType() == float.class || field.getType() == Float.class) {
                field.set(obj, new BigDecimal(value).floatValue());
            }
            else if (field.getType() == short.class || field.getType() == Short.class) {
                field.set(obj, new BigDecimal(value).shortValue());
            }
            else if (field.getType() == char.class || field.getType() == Character.class) {
                field.set(obj, value.charAt(0));
            }
        }
    }

    private static Tuple.Pair<Class<?>, Class<?>> isBoxed(Class<?> fieldType) {
        if (fieldType.equals(Integer.class)) {
            return Tuple.makePair(Integer.class, int.class);
        }
        if (fieldType.equals(Long.class)) {
            return Tuple.makePair(Long.class, long.class);
        }
        if (fieldType.equals(Boolean.class)) {
            return Tuple.makePair(Boolean.class, boolean.class);
        }
        if (fieldType.equals(Double.class)) {
            return Tuple.makePair(Double.class, double.class);
        }
        if (fieldType.equals(Byte.class)) {
            return Tuple.makePair(Byte.class, byte.class);
        }
        if (fieldType.equals(Float.class)) {
            return Tuple.makePair(Float.class, float.class);
        }
        if (fieldType.equals(Short.class)) {
            return Tuple.makePair(Short.class, short.class);
        }
        if (fieldType.equals(Character.class)) {
            return Tuple.makePair(Character.class, char.class);
        }
        return null;
    }
}
