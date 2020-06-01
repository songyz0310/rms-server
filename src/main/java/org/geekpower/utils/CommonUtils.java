package org.geekpower.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.springframework.beans.BeanUtils;

public class CommonUtils {

    public static <T> T copy(Object source, Class<T> clazz) {

        if (Objects.isNull(source) || Objects.isNull(clazz)) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, instance);
            return instance;
        }
        catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException e) {
            throw new BaseException(BaseError.UNKNOWN_ERROR.getCode(), e);
        }

    }

    public static <T> List<T> copyList(List<?> source, Class<T> clazz) {
        if (Objects.isNull(source) || Objects.isNull(clazz)) {
            return null;
        }

        if (source.isEmpty()) {
            return Collections.emptyList();
        }

        return source.stream().map(item -> copy(item, clazz)).collect(Collectors.toList());
    }

}
