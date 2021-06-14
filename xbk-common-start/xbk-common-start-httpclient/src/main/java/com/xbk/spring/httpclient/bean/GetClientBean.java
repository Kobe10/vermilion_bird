package com.xbk.spring.httpclient.bean;

import com.xbk.spring.httpclient.enums.ContentTypeEnum;

import java.util.Map;

/**
 * Created by junli on 2019-10-10
 */
public final class GetClientBean extends ClientBaseBean {

//    @Builder
    public GetClientBean(String url, Map params, Map head, ContentTypeEnum contentTypeEnum, String encode, Class resultType) {
        super(url, params, head, contentTypeEnum, encode, resultType);
    }
}
