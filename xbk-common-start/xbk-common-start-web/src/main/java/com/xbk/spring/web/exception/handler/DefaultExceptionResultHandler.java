package com.xbk.spring.web.exception.handler;

import com.xbk.core.global.exception.BizException;
import com.xbk.core.global.result.CommonResponse;
import com.xbk.core.global.result.FailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认 异常处理
 */
@Slf4j
public class DefaultExceptionResultHandler implements ExceptionResultHandler {
    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    @Override
    public CommonResponse handle(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception, HttpStatus httpStatus, CommonResponse commonResponse) {
        request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, (httpStatus == null ? HttpStatus.OK : httpStatus).value());
        if (!(exception instanceof BizException)) {
            MediaType mediaType = getProduceType(request, handlerMethod);
            if (DEFAULT_MEDIA_TYPE.equalsTypeAndSubtype(mediaType)) {
                //处理异常情况
                return FailResponse.buildRuntimeResponse(commonResponse.getCode(), commonResponse.getMessage());
            }
        }
        return commonResponse;
    }

    /**
     * 获取请求类型
     */
    public MediaType getProduceType(HttpServletRequest request, HandlerMethod handlerMethod) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept != null) {
            return contentType2MediaType(accept);
        }
        if (handlerMethod == null) {
            return DEFAULT_MEDIA_TYPE;
        }
        final RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.produces() != null && requestMapping.produces().length > 0) {
            //处理RequestMapping 含有指定输出类型
            return contentType2MediaType(requestMapping.produces()[0]);
        }
        //其次检查controller的RequestMapping
        final RequestMapping classRequestMapping = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
        if (classRequestMapping != null && classRequestMapping.produces() != null && classRequestMapping.produces().length > 0) {
            //处理RequestMapping 含有指定输出类型
            return contentType2MediaType(classRequestMapping.produces()[0]);
        }
        final ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (responseBody != null) {
            //存在注解ResponseBody,则认为返回json
            return DEFAULT_MEDIA_TYPE;
        }
        final RestController restController = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class);
        if (restController != null) {
            return DEFAULT_MEDIA_TYPE;
        }
        return DEFAULT_MEDIA_TYPE;
    }

    protected MediaType contentType2MediaType(String contentType) {
        if (contentType.startsWith(MediaType.TEXT_HTML_VALUE)) {
            return MediaType.TEXT_HTML;
        }
        if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            return MediaType.APPLICATION_JSON;
        }
        if (contentType.startsWith(MediaType.APPLICATION_XML_VALUE)) {
            return MediaType.APPLICATION_XML;
        }
        return DEFAULT_MEDIA_TYPE;
    }
}
