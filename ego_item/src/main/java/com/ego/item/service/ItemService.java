package com.ego.item.service;

import com.ego.commons.pojo.TbItemDetails;
import com.ego.item.pojo.ItemCategoryNav;
import com.ego.pojo.TbItemParam;

public interface ItemService {
    /**
     * 显示导航菜单
     * @return
     */
    ItemCategoryNav showItemCat();

    /**
     * 显示商品详情
     * @param id
     * @return
     */
    TbItemDetails showItem(Long id);

    /**
     * 显示商品描叙
     * @param itemId
     * @return
     */
    String showItemDesc(Long itemId);

    /**
     * 显示商品参数描写
     * @param paramId
     * @return
     */
    String showItemParam(Long paramId);
}
