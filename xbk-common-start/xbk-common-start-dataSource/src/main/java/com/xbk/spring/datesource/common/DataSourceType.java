package com.xbk.spring.datesource.common;

/**
 * 连接池类型
 *
 * @author: lijun
 * @date: 2020/10/29 上午11:40
 */
public final class DataSourceType {

    /**
     * druid
     */
    public final static String DRUID = "com.alibaba.druid.pool.DruidDataSource";

    /**
     * hikari
     */
    public final static String HIKARI = "com.zaxxer.hikari.HikariDataSource";

}       
