package com.xbk.spring.datesource.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class ValidateUtil {

    /**
     * 默认的 连接信息
     */
    private static String DEFAULT_URL = "xxx";

    /**
     * 连接池关键字
     */
    private static String URL_KEY_WORD = "jdbc";

    /**
     * 判断当前的字符是否是 jdbc 的连接
     *
     * @param url 连接地址
     * @return 如果是 JDBC 连接返回true
     */
    public static boolean isJDBCUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        if (DEFAULT_URL.equals(url)) {
            return false;
        }
        return url.contains(URL_KEY_WORD);
    }

}       
