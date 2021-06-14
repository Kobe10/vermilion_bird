package com.xbk.spring.datesource.core.dynamic;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSourceType {

    /**
     * 使用的数据源
     */
    @AliasFor("value")
    String name() default DynamicCommon.MASTER_NAME;

    @AliasFor("name")
    String value() default DynamicCommon.MASTER_NAME;
}
