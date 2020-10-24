package com.ego.dubbo.service;

import com.ego.commons.exception.DaoException;
import com.ego.pojo.TbContentCategory;

import java.util.List;

public interface TbContentCategoryDubboService {
    /**
     * 根据父id查询所有子类目
     * @param pid 父id
     * @return 所有子类目
     */
    List<TbContentCategory> selectByPid(Long pid);

    /**
     * 新增
     * @param category
     * @return
     */
    int insert(TbContentCategory category) throws DaoException;

    /**
     * 修改
     * @param tbContentCategory
     * @return
     */
    int updateNameById(TbContentCategory tbContentCategory);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteById(Long id) throws  DaoException;
}
