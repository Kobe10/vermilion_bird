package com.xbk.spring.datesource.config.hikari;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: lijun
 * @date: 2020/10/27 下午7:37
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HikariDataSourceProperties extends HikariConfig {

    /**
     * 占位符
     */
    public static final String DEFAULT = "unknown";

    /**
     * 连接
     */
    private String url;

    /**
     * 驱动信息
     */
    private String driverClassName;

}       
