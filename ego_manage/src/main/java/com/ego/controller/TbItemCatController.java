package com.ego.controller;

import com.ego.commons.pojo.EasyUITree;
import com.ego.service.TbItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TbItemCatController {
    @Autowired
    private TbItemCatService tbItemCatService;

    /**
     *
     * @param id id是easyui底层的参数名，固定叫做id。表示父id
     * @return
     */
    @RequestMapping("/item/cat/list")
    @ResponseBody
    public List<EasyUITree> showTree(@RequestParam(defaultValue = "0") Long id){
        return tbItemCatService.showTree(id);
    }
}
