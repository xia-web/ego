package com.ego.item.controller;

import com.ego.item.pojo.ItemCategoryNav;
import com.ego.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/rest/itemcat/all")
    @ResponseBody
    @CrossOrigin
    public ItemCategoryNav showItemCat() {
        return itemService.showItemCat();
    }

    @RequestMapping("/item/{id}.html")
    public String showItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.showItem(id));
        return "item";
    }

    @RequestMapping("/item/desc/{id}.html")
    @ResponseBody
    public String showItemDesc(@PathVariable Long id) {
        return itemService.showItemDesc(id);

    }
    @RequestMapping("/item/param/{id}.html")
    @ResponseBody
    public String showItemParam(@PathVariable Long id) {
        return itemService.showItemParam(id);

    }
}
