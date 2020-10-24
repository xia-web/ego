package com.ego.service;

import com.ego.commons.pojo.EgoResult;

public interface TbItemParamItemService {
    /**
     * 根据商品id显示商品规格参数信息
     * @param itemId
     * @return
     */
    EgoResult showItemParamItem(Long itemId);
}
