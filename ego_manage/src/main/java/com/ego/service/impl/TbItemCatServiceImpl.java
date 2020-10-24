package com.ego.service.impl;

import com.ego.commons.pojo.EasyUITree;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.pojo.TbItemCat;
import com.ego.service.TbItemCatService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TbItemCatServiceImpl implements TbItemCatService {
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;
    @Override
    public List<EasyUITree> showTree(Long pid) {
        List<EasyUITree> listTree = new ArrayList<>();
        List<TbItemCat> list = tbItemCatDubboService.selectByPid(pid);
        for(TbItemCat cat : list){
            EasyUITree tree = new EasyUITree();
            tree.setId(cat.getId());
            tree.setText(cat.getName());
            tree.setState(cat.getIsParent()?"closed":"open");
            // 有的同学总忘记这句话。页面不显示效果
            listTree.add(tree);
        }
        return listTree;
    }
}
