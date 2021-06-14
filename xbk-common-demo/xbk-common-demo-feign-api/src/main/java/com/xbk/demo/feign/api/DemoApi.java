package com.xbk.demo.feign.api;

import com.xbk.demo.feign.data.dto.rep.DemoDubboDTO;
import com.xbk.demo.feign.data.dto.req.DemoDubboVO;

import javax.validation.constraints.Max;
import java.util.List;

public interface DemoApi {

    /**
     * 通过ID 获取数据
     *
     * @param id 传入ID
     * @return 单条数据
     */
    DemoDubboVO getDemoById(@Max(value = 10, message = "xxxxxx") Long id);

    /**
     * 查询所有
     *
     * @return 所有数据
     */
    List<DemoDubboVO> getAll();

    /**
     * 保存数据
     */
    void saveInfo(DemoDubboDTO demoDubboDTO);
}       
    

