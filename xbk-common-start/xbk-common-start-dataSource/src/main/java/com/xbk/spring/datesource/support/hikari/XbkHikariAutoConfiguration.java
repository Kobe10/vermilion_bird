package com.xbk.spring.datesource.support.hikari;

import com.xbk.spring.datesource.common.Common;
import com.xbk.spring.datesource.common.DataSourceType;
import com.xbk.spring.datesource.common.Prefix;
import com.xbk.spring.datesource.config.hikari.HikariDataSourceProperties;
import com.xbk.spring.datesource.config.hikari.MultiHikariProperties;
import com.xbk.spring.datesource.core.factory.HikariDataSourceFactory;
import com.xbk.spring.datesource.model.MultiDataSources;
import com.xbk.spring.datesource.util.ObjectUtil;
import com.xbk.spring.datesource.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(name = Prefix.DATA_SOURCE_TYPE, havingValue = DataSourceType.HIKARI)
@ConditionalOnClass(name = "com.zaxxer.hikari.HikariDataSource")
@EnableConfigurationProperties(MultiHikariProperties.class)
public class XbkHikariAutoConfiguration {

    /**
     * hikari 主数据源配置
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    @AutoConfigureBefore(DataSourceAutoConfiguration.class)
    protected static class HikariDataSourceConfiguration {

        private final MultiHikariProperties hikariProperties;

        @Bean(name = Common.MASTER_DATA_SOURCE_NAME)
        @Primary
        @ConditionalOnMissingBean(name = Common.MASTER_DATA_SOURCE_NAME)
        public DataSource dataSource() {
            final DataSource dataSource = HikariDataSourceFactory.createDataSource(hikariProperties);
            log.info(">>>>> 初始化数据库连接（master - hikari）：{}", hikariProperties.getUrl());
            return dataSource;
        }
    }

    /**
     * hikari 多数据源配置
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    protected static class DruidMultiDataSourcesConfiguration {

        private final MultiHikariProperties multiDruidProperties;

        @ConditionalOnMissingBean(name = Common.MULTI_DATA_SOURCE_NAME)
        @Bean(name = Common.MULTI_DATA_SOURCE_NAME)
        public MultiDataSources dataSourcesRegister() {
            Map<String, DataSource> dataSourcesMap = new HashMap<>();
            if (multiDruidProperties == null
                    || multiDruidProperties.getDynamicDataSource() == null
                    || multiDruidProperties.getDynamicDataSource().size() == 0) {
                return new MultiDataSources(dataSourcesMap);
            }
            Map<String, HikariDataSourceProperties> dataSources = multiDruidProperties.getDynamicDataSource();
            dataSources.entrySet().stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .filter(entry -> ValidateUtil.isJDBCUrl(entry.getValue().getUrl()))
                    .forEach(entry -> {
                        ObjectUtil.combine(multiDruidProperties, entry.getValue());
                        dataSourcesMap.put(entry.getKey(), HikariDataSourceFactory.createDataSource(entry.getValue()));
                        log.info(">>>>> 初始化数据库连接（{} - hikari）：{}", entry.getKey(), entry.getValue().getUrl());
                    });
            return new MultiDataSources(dataSourcesMap);
        }
    }
}
