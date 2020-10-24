package com.ego.service;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItemParam;

public interface TbItemParamService {
    EasyUIDatagrid showItemParam(int page,int rows);

    /**
     * 根据类目id查询规格参数模板
     * @param catId
     * @return
     */
    EgoResult showItemParamByCatid(Long catId);

    /**
     * 新增规格模板
     * @param tbItemParam
     * @return
     */
    EgoResult insert(TbItemParam tbItemParam);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    EgoResult delete(long [] ids);

}
