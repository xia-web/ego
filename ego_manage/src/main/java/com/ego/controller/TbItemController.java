package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItem;
import com.ego.service.TbItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
public class TbItemController {
    @Autowired
    private TbItemService tbItemService;

    /**
     * 分页显示商品
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDatagrid showItem(int page,int rows){
        return tbItemService.showItem(page,rows);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/rest/item/delete")
    @ResponseBody
    public EgoResult delete(long[] ids){
        return tbItemService.updateStatus(ids,3);
    }
    /**
     * 上架
     * @param ids
     * @return
     */
    @RequestMapping("/rest/item/reshelf")
    @ResponseBody
    public EgoResult reshelf(long[] ids){
        return tbItemService.updateStatus(ids,1);
    }
    /**
     * 下架
     * @param ids
     * @return
     */
    @RequestMapping("/rest/item/instock")
    @ResponseBody
    public EgoResult instock(long[] ids){
        return tbItemService.updateStatus(ids,2);
    }

    /**
     * 商品新增功能
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping("/item/save")
    @ResponseBody
    public EgoResult insert(TbItem item,String desc,String itemParams){
        return tbItemService.insert(item,desc,itemParams);
    }

    /**
     * 修改
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping("/rest/item/update")
    @ResponseBody
    public EgoResult update(TbItem item,String desc,String itemParams,long itemParamId){
        return tbItemService.update(item,desc,itemParams,itemParamId);
    }
}
