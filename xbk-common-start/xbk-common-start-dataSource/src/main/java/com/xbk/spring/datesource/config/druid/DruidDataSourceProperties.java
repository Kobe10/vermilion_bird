package com.xbk.spring.datesource.config.druid;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

@Data
@NoArgsConstructor
public class DruidDataSourceProperties {

    private String url;

    private String username;

    private String password;

    /**
     * 是否只读库
     */
    private boolean readonly;
    /**
     * 权重，方便负载均衡使用
     */
    private int weight;

    /**
     * 驱动
     */
    private String driverClassName;

    /** ---------------- **/
    /**
     * 连接池最大值
     */
    private Integer maxActive = 200;

    /**
     * 初始大小
     */
    private Integer initialSize = 10;

    /**
     * 连接等待最大时间
     */
    private Integer maxWait = 60000;

    /**
     * 连接池最小值
     */
    private Integer minIdle = 10;

    /** ---------------- **/
    /**
     * 有两个含义： 1)Destroy线程会检测连接的间隔时间2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
     */
    private Integer timeBetweenEvictionRunsMillis = 60000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private Integer minEvictableIdleTimeMillis = 300000;

    private String validationQuery = "select 'x'";

    /**
     * ----------------
     **/
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = false;

    /**
     * ----------------
     **/
    private boolean poolPreparedStatements = true;

    private Integer maxPoolPreparedStatementPerConnectionSize = 20;

    private Integer psCacheSize;

    private String filters = "stat,slf4j";

    private Properties connectionProperties;
}
