package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbItemParam;

import java.util.List;

public interface TbItemParamDubboService {
    /**
     * 分页查询
     * @param pageNumber 第几页
     * @param pageSize 每页大小
     * @return 当前页数据
     */
    List<TbItemParam> selectByPage(int pageNumber, int pageSize);

    /**
     * 查询总数量
     * @return
     */
    long selectCount();

    /**
     * 根据商品类目查询书商品规格参数模板信息
     * @param catId 类目id
     * @return 模板信息
     */
    TbItemParam selectByCatid(Long catId);

    /**
     * 新增
     * @param tbItemParam
     * @return
     */
    int insert(TbItemParam tbItemParam);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    int delete(long[] ids)  throws DaoException;
}
