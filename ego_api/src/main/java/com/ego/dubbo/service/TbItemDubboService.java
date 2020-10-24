package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;

import java.util.List;

/**
 * 商品表数据库操作
 */
public interface TbItemDubboService {
    /**
     * 分页查询
     * @param pageSize 每页大小
     * @param pageNumber 页码
     * @return 当前页显示数据
     */
    List<TbItem> selectByPage(int pageSize,int pageNumber);

    /**
     * 查询总条数
     * @return 总条数
     */
    long selectCount();

    /**
     * 事务一定要写在provider方
     * 批量修改
     * @param ids 所有要修改的id
     * @param status 修改的状态值
     * @return 成功1，失败0
     */
    int updateStatusByIds(long[] ids,int status) throws DaoException;

    /**
     * 新增
     * @param tbItem
     * @param tbItemDesc
     * @return
     * @throws DaoException
     */
    int insert(TbItem tbItem, TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) throws DaoException;

    /**
     * 修改
     * @param tbItem
     * @param tbItemDesc
     * @return
     * @throws DaoException
     */
    int update(TbItem tbItem,TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) throws DaoException;

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    TbItem selectById(Long id);
}
