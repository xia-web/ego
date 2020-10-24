package com.ego.service;

import com.ego.commons.pojo.EasyUITree;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContentCategory;

import java.util.List;

public interface TbContentCategoryService {
    /**
     * 显示内容分类树状菜单
     * @param pid 父id
     * @return easyui tree要的数据
     */
    List<EasyUITree> showContentCategory(Long pid);

    /**
     * 新增
     * @param tbContentCategory
     * @return
     */
    EgoResult insert(TbContentCategory tbContentCategory);

    /**
     * 修改分类名称
     * @param tbContentCategory
     * @return
     */
    EgoResult update(TbContentCategory tbContentCategory);

    /**
     * 删除分类
     * @param id
     * @return
     */
    EgoResult delete(Long id);
}
