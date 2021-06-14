package com.xbk.spring.datesource.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class ObjectUtil {

    /**
     * 属性合并
     *
     * @param source 原对象
     * @param target 目标对象
     */
    public static void combine(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        Assert.state(target.getClass().isAssignableFrom(source.getClass()), "类型不一致");
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                //只设置为null的
                if (readMethod.invoke(target) == null) {
                    Object value = readMethod.invoke(source);
                    if (value != null) {
                        writeMethod.invoke(target, value);
                    }
                }
            }
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}       
