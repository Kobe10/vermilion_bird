package com.xbk.spring.datesource.support;

import com.xbk.spring.datesource.common.Prefix;
import com.xbk.spring.datesource.support.druid.XbkDruidAutoConfiguration;
import com.xbk.spring.datesource.support.hikari.XbkHikariAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = XbkDataSourceAutoConfiguration.NAME, havingValue = XbkDataSourceAutoConfiguration.NAME_DEFAULT_VALUE, matchIfMissing = true)
public class XbkDataSourceAutoConfiguration {

    /**
     * 默认开启该配置对应的名称
     */
    static final String NAME = Prefix.DATA_SOURCE_AUTO_PROPERTIES + ".enable";

    /**
     * 默认开启该配置对应的名称 - 值
     */
    static final String NAME_DEFAULT_VALUE = "true";

    /**
     * Druid 自动注入
     */
    @Configuration
    @Import(XbkDruidAutoConfiguration.class)
    static class DruidAutoConfiguration {

    }

    /**
     * Hikari 自动注入
     */
    @Configuration
    @Import(XbkHikariAutoConfiguration.class)
    static class HikariAutoConfiguration {

    }
}       
