package com.xbk.spring.datesource.support;

import com.xbk.spring.datesource.common.Common;
import com.xbk.spring.datesource.config.druid.MultiDruidProperties;
import com.xbk.spring.datesource.core.dynamic.AbstractDynamicAspect;
import com.xbk.spring.datesource.core.dynamic.DynamicCommon;
import com.xbk.spring.datesource.core.dynamic.DynamicDataSource;
import com.xbk.spring.datesource.model.MultiDataSources;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EnableConfigurationProperties(MultiDruidProperties.class)
@AutoConfigureAfter(value = {DataSource.class, MultiDataSources.class})
public class XbkDynamicAutoConfiguration {

    /**
     * 动态数据源
     */
    @Bean(name = DynamicCommon.DYNAMIC_NAME)
    @ConditionalOnBean(name = {Common.MASTER_DATA_SOURCE_NAME, Common.MULTI_DATA_SOURCE_NAME})
    public DynamicDataSource dynamicDataSource(
            @Qualifier(Common.MASTER_DATA_SOURCE_NAME) DataSource dataSource,
            @Qualifier(Common.MULTI_DATA_SOURCE_NAME) MultiDataSources multiDataSources) {
        final DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        if (Objects.nonNull(multiDataSources) && Objects.nonNull(multiDataSources.getDataSources())) {
            final Map<Object, Object> targetData = multiDataSources.getDataSources().entrySet()
                    .stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
            //把主数据库 作为 master 添加进选项中
            targetData.put(DynamicCommon.MASTER_NAME, dataSource);
            //生成最终的数据库选择列表
            dynamicDataSource.setTargetDataSources(targetData);
        }
        return dynamicDataSource;
    }

    /**
     * 注册一个 多数据源切面
     */
    @Aspect
    @RequiredArgsConstructor
    @Configuration
    @ConditionalOnMissingBean(AbstractDynamicAspect.class)
    public static class DynamicAspectConfiguration extends AbstractDynamicAspect {

    }
}
