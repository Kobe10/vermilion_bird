package com.xbk.spring.datesource.core.factory;

import com.xbk.spring.datesource.config.hikari.HikariDataSourceProperties;
import com.xbk.spring.datesource.config.hikari.MultiHikariProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@UtilityClass
public class HikariDataSourceFactory {

    /**
     * HikariDataSource 创建
     */
    public static DataSource createDataSource(HikariDataSourceProperties config) {
        config.setDriverClassName(StringUtils.hasText(config.getDriverClassName())
                ? config.getDriverClassName()
                : "com.mysql.jdbc.Driver");
        config.setJdbcUrl(config.getUrl());
        //使用默认值通配
        if (MultiHikariProperties.DEFAULT.equals(config.getConnectionTestQuery())) {
            config.setConnectionTestQuery(null);
        }
        HikariDataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }
}