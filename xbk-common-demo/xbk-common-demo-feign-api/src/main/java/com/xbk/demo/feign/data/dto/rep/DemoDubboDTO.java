package com.xbk.demo.feign.data.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
public class DemoDubboDTO implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * num
     */
    private Integer num;
}       
    

