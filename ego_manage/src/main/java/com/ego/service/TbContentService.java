package com.ego.service;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbContent;

public interface TbContentService {
    /**
     * 分页查询
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    EasyUIDatagrid showContent(Long categoryId,int page,int rows);

    /**
     * 新增
     * @param tbContent
     * @return
     */
    EgoResult insert(TbContent tbContent);

    /**
     * 修改
     * @param tbContent
     * @return
     */
    EgoResult update(TbContent tbContent);

    /**
     * 批量删除
     * @param ids 要删的id
     * @return
     */
    EgoResult delete(long [] ids);
}
