package com.xbk.demo.feign.data.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
public class DemoDubboVO implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private Integer num;
}       
    

