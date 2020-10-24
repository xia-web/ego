package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUITree;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentCategoryDubboService;
import com.ego.pojo.TbContentCategory;
import com.ego.service.TbContentCategoryService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TbContentCategoryServiceImpl implements TbContentCategoryService {
    @Reference
    private TbContentCategoryDubboService tbContentCategoryDubboService;
    @Override
    public List<EasyUITree> showContentCategory(Long pid) {
        List<TbContentCategory> list = tbContentCategoryDubboService.selectByPid(pid);
        List<EasyUITree> listTree = new ArrayList<>();
        for(TbContentCategory category: list){
            EasyUITree tree = new EasyUITree();
            tree.setId(category.getId());
            tree.setState(category.getIsParent()?"closed":"open");
            tree.setText(category.getName());
            listTree.add(tree);
        }
        return listTree;
    }

    @Override
    public EgoResult insert(TbContentCategory tbContentCategory) {
        Date date = new Date();
        tbContentCategory.setUpdated(date);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(date);
        tbContentCategory.setId(IDUtils.genItemId());
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setStatus(1);
        try {
            int index = tbContentCategoryDubboService.insert(tbContentCategory);
            if(index==1){
                return EgoResult.ok(tbContentCategory);
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("创建失败");
    }

    @Override
    public EgoResult update(TbContentCategory tbContentCategory) {
        tbContentCategory.setUpdated(new Date());
        int index = tbContentCategoryDubboService.updateNameById(tbContentCategory);
        if(index==1){
            return EgoResult.ok();
        }
        return EgoResult.error("重命名失败");
    }

    @Override
    public EgoResult delete(Long id) {
        try {
            int index = tbContentCategoryDubboService.deleteById(id);
            if(index==1){
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("删除失败");
    }
}
