package com.swcs.esop.api.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefUtil {
    static Logger logger = LoggerFactory.getLogger(RefUtil.class);

    public static <T> boolean hasField(T record, String fieldName) {
        Class<?> clazz = record.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                return true;
            }
        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
        }
        return false;
    }

    public static <T> boolean hasField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                return true;
            }
        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
        }
        return false;
    }

    public static <T> boolean setField(T record, String fieldName, Object value) {
        Class<?> clazz = record.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(record, value);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public static Object getFieldValue(Object obj, Field field) {
        return getFieldValue(obj, field.getName());
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        Class<?> clz = obj.getClass();
        try {
            Method method = clz.getMethod("get" + StringUtils.capitalize(fieldName));
            return method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object obj, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
