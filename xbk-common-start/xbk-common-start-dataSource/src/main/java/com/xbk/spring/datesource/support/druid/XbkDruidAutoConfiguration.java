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
     * druid ??????????????????
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
            log.info(">>>>> ???????????????????????????master - druid??????{}", druidProperties.getUrl());
            return dataSource;
        }
    }

    /**
     * druid ??????????????????
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
                            log.info(">>>>> ???????????????????????????{} - druid??????{}", entry.getKey(), entry.getValue().getUrl());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
            return new MultiDataSources(dataSourcesMap);
        }
    }

    /**
     * druid ????????????
     */
    @Slf4j
    @RequiredArgsConstructor
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = CrmDruidMonitorConfiguration.NAME, havingValue = CrmDruidMonitorConfiguration.NAME_DEFAULT_VALUE)
    protected static class CrmDruidMonitorConfiguration {

        /**
         * ????????????????????????????????????
         */
        static final String NAME = Prefix.DATA_SOURCE_MONITOR_PROPERTIES + ".enable";

        /**
         * ???????????????????????????????????? - ???
         */
        static final String NAME_DEFAULT_VALUE = "true";

        /**
         * druid ?????????????????????
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
            // IP?????????
            if (!StringUtils.isEmpty(monitor.getAllow())) {
                servletRegistrationBean.addInitParameter("allow", monitor.getAllow());
            }
            // IP?????????(??????????????????deny?????????allow)
            if (!StringUtils.isEmpty(monitor.getDeny())) {
                servletRegistrationBean.addInitParameter("deny", monitor.getDeny());
            }
            //?????????????????????
            servletRegistrationBean.addInitParameter("loginUsername", monitor.getLoginUsername());
            servletRegistrationBean.addInitParameter("loginPassword", monitor.getLoginPassword());
            //???????????????????????? ??????HTML???????????????Reset All?????????
            servletRegistrationBean.addInitParameter("resetEnable", monitor.getResetEnable());
            log.info(">>>>> ????????????druid????????? ??????");
            return servletRegistrationBean;
        }

        /**
         * WebStatFilter
         */
        @Bean
        public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
            FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
            MultiDruidProperties.MonitorProperties monitor = multiDruidProperties.getMonitor();
            //??????????????????.
            filterRegistrationBean.addUrlPatterns(monitor.getDruidWebStatFilter());
            //????????????????????????????????????.
            filterRegistrationBean.addInitParameter("exclusions", monitor.getExclusions());
            //????????????sessionStatMaxCount???1000???
            filterRegistrationBean.addInitParameter("sessionStatMaxCount", monitor.getSessionStatMaxCount());
            //??????session ????????????
            filterRegistrationBean.addInitParameter("sessionStatEnable", monitor.getSessionStatEnable());
            return filterRegistrationBean;
        }
    }
}
