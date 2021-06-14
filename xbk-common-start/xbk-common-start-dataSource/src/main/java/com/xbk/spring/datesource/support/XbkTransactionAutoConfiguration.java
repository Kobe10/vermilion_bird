package com.xbk.spring.datesource.support;

import com.xbk.spring.datesource.common.Common;
import com.xbk.spring.datesource.common.Prefix;
import com.xbk.spring.datesource.config.druid.MultiDruidProperties;
import com.xbk.spring.datesource.core.dynamic.DynamicCommon;
import com.xbk.spring.datesource.core.dynamic.DynamicDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(name = XbkTransactionAutoConfiguration.NAME, havingValue = XbkTransactionAutoConfiguration.NAME_DEFAULT_VALUE, matchIfMissing = true)
@EnableConfigurationProperties(MultiDruidProperties.class)
@AutoConfigureAfter(value = {MultiDruidProperties.class, DataSource.class})
@Import(value = {XbkDynamicAutoConfiguration.class, XbkDynamicAutoConfiguration.DynamicAspectConfiguration.class})
public class XbkTransactionAutoConfiguration {

    /**
     * 默认开启该配置对应的名称
     */
    static final String NAME = Prefix.DATA_SOURCE_AUTO_PROPERTIES + ".enable";

    /**
     * 默认开启该配置对应的名称 - 值
     */
    static final String NAME_DEFAULT_VALUE = "true";

    /**
     * 配置事物管理器
     */
    @Bean(name = Common.MYBATIS_TRANSACTION_MANAGER_NAME)
    @ConditionalOnBean(name = DynamicCommon.DYNAMIC_NAME)
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(
            @Qualifier(DynamicCommon.DYNAMIC_NAME) DynamicDataSource dynamicDataSource) {
        log.info(">>>>> 初始化【DataSourceTransactionManager】 完成");
        return new DataSourceTransactionManager(dynamicDataSource);
    }

}
