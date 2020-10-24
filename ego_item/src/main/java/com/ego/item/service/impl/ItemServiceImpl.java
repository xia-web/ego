package com.ego.item.service.impl;

import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.*;
import com.ego.item.pojo.CategoryNode;
import com.ego.item.pojo.ItemCategoryNav;
import com.ego.item.pojo.Param;
import com.ego.item.pojo.ParamItem;
import com.ego.item.service.ItemService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;
    @Reference
    private TbItemDescDubboService tbItemDescDubboService;
    @Reference
    private TbItemDubboService tbItemDubboService;
    @Reference
    private TbItemParamItemDubboService tbItemParamItemDubboService;

    @Override
    @Cacheable(cacheNames = "com.ego.item",key = "'showItemCat'")
    public ItemCategoryNav showItemCat() {
        System.out.println("执行方法");
        ItemCategoryNav itemCategoryNav = new ItemCategoryNav();
        itemCategoryNav.setData(getAllItemCat(0L));
        return itemCategoryNav;
    }

    @Override
    @Cacheable(cacheNames = "com.ego.item", key = "'details:' + #id")
    public TbItemDetails showItem(Long id) {
        TbItem tbItem = tbItemDubboService.selectById(id);
        TbItemDetails details = new TbItemDetails();
        details.setId(tbItem.getId());
        details.setPrice(tbItem.getPrice());
        details.setSellPoint(tbItem.getSellPoint());
        details.setTitle(tbItem.getTitle());
        String img = tbItem.getImage();
        details.setImages(img!=null && !img.equals("")?img.split(","):new String[1]);
        System.out.println(details.getImages().length);
        return details;
    }

    @Override
    public String showItemDesc(Long itemId) {
        TbItemDesc tbItemDesc = tbItemDescDubboService.selectById(itemId);
        return tbItemDesc.getItemDesc();
    }

    @Override
    public String showItemParam(Long paramId) {
        TbItemParamItem tbItemParamItem = tbItemParamItemDubboService.selectByItemId(paramId);
        String paramData = tbItemParamItem.getParamData();
        List<ParamItem> paramItems = JsonUtils.jsonToList(paramData, ParamItem.class);
        StringBuffer msg = new StringBuffer();

        for (ParamItem p: paramItems) {
            msg.append("<table  style='color:gray;' width:'500px' cellpadding='10'>");
            for (int i = 0; i < p.getParams().size(); i++) {
                msg.append("<tr>");
                if (i == 0){
                    msg.append("<td style='width:150px; text-align:center' >" +p.getGroup() +"</td>");
                    msg.append("<tr>");
                    msg.append("<td style='width:300px; text-align:right'>" + p.getParams().get(i).getK() + "</td>");
                    msg.append("<td style='width:200px; text-align:left'>" + p.getParams().get(i).getV() + "</td>");
                    msg.append("</tr>");
                }else {
                    msg.append("<td style='width:300px; text-align:right'>" + p.getParams().get(i).getK() + "</td>");
                    msg.append("<td style='width:200px; text-align:left'>" + p.getParams().get(i).getV() + "</td>");
                }
                msg.append("</tr>");
            }
            msg.append("</table>");
            msg.append("<hr style='color:gray;'/>");
        }
        return msg.toString();
    }

    private List<Object> getAllItemCat(Long parentId){
        List<TbItemCat> list = tbItemCatDubboService.selectByPid(parentId);
        List<Object> listResult = new ArrayList<>();
        // 一个cat对应一个菜单项目。前两层类型都是CategoryNode类型，第三层是String
        for(TbItemCat cat : list){
            // 说明是第一层或第二层。
            if(cat.getIsParent()){
                CategoryNode node = new CategoryNode();
                node.setU("/products/"+cat.getId()+".html");
                node.setN("<a href='/products/"+cat.getId()+".html'>"+cat.getName()+"</a>");
                node.setI(getAllItemCat(cat.getId()));
                listResult.add(node);
            }else{
                listResult.add("/products/"+cat.getId()+".html|"+cat.getName());
            }

        }
        return listResult;
    }
}
