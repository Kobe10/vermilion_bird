package com.xbk.spring.datesource.config.hikari;

import com.xbk.spring.datesource.common.Prefix;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

/**
 * Hikari 多数据源配置
 *
 * @author: lijun
 * @date: 2020/10/27 下午7:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = Prefix.DATA_SOURCE_PROPERTIES_HIKARI)
public class MultiHikariProperties extends HikariDataSourceProperties {

    /**
     * 多数据源信息
     */
    private HashMap<String, HikariDataSourceProperties> dynamicDataSource;

}       
