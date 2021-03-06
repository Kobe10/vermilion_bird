package com.xbk.spring.mybatis.service.impl;

import com.xbk.spring.mybatis.mapper.BaseComponentMapper;
import com.xbk.spring.mybatis.model.BaseEntity;
import com.xbk.spring.mybatis.model.BaseStatusEnum;
import com.xbk.spring.mybatis.service.BaseDecoratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class BaseDecoratorServiceImpl<T extends BaseEntity> extends AbstractServiceImpl<T> implements BaseDecoratorService<T> {

    @Autowired
    private Mapper<T> baseMapper;

    @Autowired
    private BaseComponentMapper<T> baseComponentMapper;

    @Override
    Mapper<T> baseMapper() {
        return baseMapper;
    }

    @Override
    BaseComponentMapper<T> baseComponentMapper() {
        return baseComponentMapper;
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        checkIllegalId(key);
        T result = baseMapper.selectByPrimaryKey(key);
        if (null != result) {
            if (!BaseStatusEnum.isNormal(result.getIsDel())) {
                return null;
            }
            return result;
        }
        return null;
    }

    @Override
    public T selectOne(T t) {
        t.setIsDel(BaseStatusEnum.NORMAL.getCode());
        return baseMapper.selectOne(t);
    }

    @Override
    public int selectCount(T t) {
        t.setIsDel(BaseStatusEnum.NORMAL.getCode());
        return baseMapper.selectCount(t);
    }

    @Override
    public List<T> selectByExample(Example example) {
        fixExample(example);
        return baseMapper.selectByExample(example);
    }

    @Override
    public int selectCountByExample(Example example) {
        fixExample(example);
        return baseMapper.selectCountByExample(example);
    }

    @Override
    public List<T> selectAll() {
        List<T> list = baseMapper.selectAll();
        List<T> collect = list.parallelStream()
                .filter(data -> Objects.nonNull(data.getIsDel()))
                .filter(data -> BaseStatusEnum.isNormal(data.getIsDel()))
                .collect(Collectors.toList());
        return collect;
    }

    @Transactional(rollbackFor = SQLException.class)
    @Override
    public int deleteByPrimaryKey(Long id) {
        if (null == id || 0 >= id) {
            return 0;
        }
        WeekendSqls<T> sql = WeekendSqls.custom();
        sql.andEqualTo(T::getId, id);
        Example example = Example.builder(currentModelClass())
                .where(sql)
                .build();
        return deleteByExample(example);
    }

    @Transactional(rollbackFor = SQLException.class)
    @Override
    public int deleteByExample(Example example) {
        try {
            T deleteEntity = currentModelClass().newInstance();
            fixExample(example);
            deleteEntity.setIsDel(BaseStatusEnum.DELETED.getCode());
            buildUpdateInfo(deleteEntity);
            return baseMapper.updateByExampleSelective(deleteEntity, example);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Transactional(rollbackFor = SQLException.class)
    @Override
    public int deleteByPrimaryKeys(Collection primaryKeys) {
        if (primaryKeys == null || primaryKeys.size() == 0) {
            log.info("???????????????id ???????????????0");
            return 0;
        }
        WeekendSqls<T> sql = WeekendSqls.custom();
        sql.andIn(T::getId, primaryKeys);

        Example example = Example.builder(currentModelClass())
                .where(sql)
                .build();
        fixExample(example);
        return deleteByExample(example);
    }

    @Transactional(rollbackFor = SQLException.class)
    @Override
    public int updateByExample(T t, Example example) {
        fixExample(example);
        buildUpdateInfo(t);
        return baseMapper.updateByExample(t, example);
    }

    @Transactional(rollbackFor = SQLException.class)
    @Override
    public int updateByExampleSelective(T t, Example example) {
        fixExample(example);
        buildUpdateInfo(t);
        return baseMapper.updateByExampleSelective(t, example);
    }

    protected static void fixExample(Example example) {
        example.and().andEqualTo("isDel", BaseStatusEnum.NORMAL.getCode());
        example.setOrderByClause(StringUtils.isEmpty(example.getOrderByClause()) ?
                "id DESC" : example.getOrderByClause() + ",id DESC");
    }

    protected static Date timestampInit() {
        return new Date();
    }

    @Override
    public void buildInsertInfo(T t) {
        t.setCreateTime(timestampInit());
        t.setIsDel(BaseStatusEnum.NORMAL.getCode());
        t.setLastModifyTime(timestampInit());
    }
}
