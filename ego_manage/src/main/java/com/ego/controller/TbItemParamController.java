package com.ego.controller;

import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbItemParam;
import com.ego.service.TbItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *@RestController = @Controller + 每个方法上面自动添加@ResponseBody
 */
@RestController
public class TbItemParamController {
    @Autowired
    private TbItemParamService tbItemParamService;


    @RequestMapping("/item/param/list")
    public EasyUIDatagrid showItemparam(int page,int rows){
        return tbItemParamService.showItemParam(page,rows);
    }

    /**
     * 根据类目id查询规格模板
     * @param id
     * @return
     */
    @RequestMapping("/item/param/query/itemcatid/{id}")
    public EgoResult showItemParamByCatId(@PathVariable Long id){
        return tbItemParamService.showItemParamByCatid(id);
    }

    @RequestMapping("/item/param/save/{catId}")
    public EgoResult insert(TbItemParam tbItemParam,@PathVariable Long catId){
        tbItemParam.setItemCatId(catId);
        return tbItemParamService.insert(tbItemParam);
    }


    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/item/param/delete")
    public EgoResult delete(long[] ids){
        return tbItemParamService.delete(ids);
    }
}
