package com.xbk.spring.datesource.support.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.xbk.spring.datesource.common.Common;
import com.xbk.spring.datesource.common.DataSourceType;
import com.xbk.spring.datesource.common.Prefix;
import com.xbk.spring.datesource.config.druid.DruidDataSourceProperties;
import com.xbk.spring.datesource.config.druid.MultiDruidProperties;
import com.xbk.spring.datesource.core.DruidDataSourceFactory;
import com.xbk.spring.datesource.model.MultiDataSources;
import com.xbk.spring.datesource.util.ObjectUtil;
import com.xbk.spring.datesource.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(name = Prefix.DATA_SOURCE_TYPE, havingValue = DataSourceType.DRUID, matchIfMissing = true)
@ConditionalOnClass(name = "com.alibaba.druid.pool.DruidDataSource")
@EnableConfigurationProperties(MultiDruidProperties.class)
public class XbkDruidAutoConfiguration {

    /**
     * druid 主数据源配置
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    @AutoConfigureBefore(DataSourceAutoConfiguration.class)
    protected static class DruidDataSourceConfiguration {

        private final MultiDruidProperties druidProperties;

        @Bean(name = Common.MASTER_DATA_SOURCE_NAME)
        @Primary
        @ConditionalOnMissingBean(name = Common.MASTER_DATA_SOURCE_NAME)
        public DataSource dataSource() throws SQLException {
            final DataSource dataSource = DruidDataSourceFactory.createDataSource(druidProperties);
            log.info(">>>>> 初始化数据库连接（master - druid）：{}", druidProperties.getUrl());
            return dataSource;
        }
    }

    /**
     * druid 多数据源配置
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    protected static class DruidMultiDataSourcesConfiguration {

        private final MultiDruidProperties multiDruidProperties;

        @ConditionalOnMissingBean(name = Common.MULTI_DATA_SOURCE_NAME)
        @Bean(name = Common.MULTI_DATA_SOURCE_NAME)
        public MultiDataSources dataSourcesRegister() {
            Map<String, DataSource> dataSourcesMap = new HashMap<>();
            if (multiDruidProperties == null
                    || multiDruidProperties.getDynamicDataSource() == null
                    || multiDruidProperties.getDynamicDataSource().size() == 0) {
                return new MultiDataSources(dataSourcesMap);
            }
            Map<String, DruidDataSourceProperties> dataSources = multiDruidProperties.getDynamicDataSource();
            dataSources.entrySet().stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .filter(entry -> ValidateUtil.isJDBCUrl(entry.getValue().getUrl()))
                    .forEach(entry -> {
                        ObjectUtil.combine(multiDruidProperties, entry.getValue());
                        try {
                            dataSourcesMap.put(entry.getKey(), DruidDataSourceFactory.createDataSource(entry.getValue()));
                            log.info(">>>>> 初始化数据库连接（{} - druid）：{}", entry.getKey(), entry.getValue().getUrl());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
            return new MultiDataSources(dataSourcesMap);
        }
    }

    /**
     * druid 监控配置
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = CrmDruidMonitorConfiguration.NAME, havingValue = CrmDruidMonitorConfiguration.NAME_DEFAULT_VALUE)
    protected static class CrmDruidMonitorConfiguration {

        /**
         * 默认开启该配置对应的名称
         */
        static final String NAME = Prefix.DATA_SOURCE_MONITOR_PROPERTIES + ".enable";

        /**
         * 默认开启该配置对应的名称 - 值
         */
        static final String NAME_DEFAULT_VALUE = "true";

        /**
         * druid 数据源监控配置
         */
        private final MultiDruidProperties multiDruidProperties;

        /**
         * StatViewServlet
         */
        @Bean
        public ServletRegistrationBean<StatViewServlet> druidServlet() {
            MultiDruidProperties.MonitorProperties monitor = multiDruidProperties.getMonitor();
            ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>();
            servletRegistrationBean.setServlet(new StatViewServlet());
            final ArrayList<String> list = new ArrayList<>();
            list.add(monitor.getDruidStatView());
            servletRegistrationBean.setUrlMappings(list);
            // IP白名单
            if (!StringUtils.isEmpty(monitor.getAllow())) {
                servletRegistrationBean.addInitParameter("allow", monitor.getAllow());
            }
            // IP黑名单(共同存在时，deny优先于allow)
            if (!StringUtils.isEmpty(monitor.getDeny())) {
                servletRegistrationBean.addInitParameter("deny", monitor.getDeny());
            }
            //控制台管理用户
            servletRegistrationBean.addInitParameter("loginUsername", monitor.getLoginUsername());
            servletRegistrationBean.addInitParameter("loginPassword", monitor.getLoginPassword());
            //是否能够重置数据 禁用HTML页面上的“Reset All”功能
            servletRegistrationBean.addInitParameter("resetEnable", monitor.getResetEnable());
            log.info(">>>>> 初始化【druid监控】 完成");
            return servletRegistrationBean;
        }

        /**
         * WebStatFilter
         */
        @Bean
        public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
            FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
            MultiDruidProperties.MonitorProperties monitor = multiDruidProperties.getMonitor();
            //添加过滤规则.
            filterRegistrationBean.addUrlPatterns(monitor.getDruidWebStatFilter());
            //添加不需要忽略的格式信息.
            filterRegistrationBean.addInitParameter("exclusions", monitor.getExclusions());
            //初始缺省sessionStatMaxCount是1000个
            filterRegistrationBean.addInitParameter("sessionStatMaxCount", monitor.getSessionStatMaxCount());
            //关闭session 统计功能
            filterRegistrationBean.addInitParameter("sessionStatEnable", monitor.getSessionStatEnable());
            return filterRegistrationBean;
        }
    }
}
