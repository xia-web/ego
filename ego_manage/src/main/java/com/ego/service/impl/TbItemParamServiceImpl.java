package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemParamDubboService;
import com.ego.pojo.TbItemParam;
import com.ego.pojo.TbItemParamChild;
import com.ego.service.TbItemParamService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TbItemParamServiceImpl implements TbItemParamService {
    @Reference
    private TbItemParamDubboService tbItemParamDubboService;
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;
    @Override
    public EasyUIDatagrid showItemParam(int page, int rows) {
        List<TbItemParam> list = tbItemParamDubboService.selectByPage(page, rows);
        List<TbItemParamChild> listChild = new ArrayList<>();
        for(TbItemParam param : list){
            TbItemParamChild child = new TbItemParamChild();
//            child.setId(param.getId());
//            child.setCreated(param.getCreated());
//            child.setUpdated(param.getUpdated());
//            child.setParamData(param.getParamData());
//            child.setItemCatId(param.getItemCatId());
            // 第一个参数： 源对象  第二个参数：新对象。
            // 按照同名属性把源对象中数据拷贝到新对象里。
            BeanUtils.copyProperties(param,child);
            child.setItemCatName(tbItemCatDubboService.selectByid(param.getItemCatId()).getName());
            listChild.add(child);
        }
        long total = tbItemParamDubboService.selectCount();
        return new EasyUIDatagrid(listChild,total);
    }

    @Override
    public EgoResult showItemParamByCatid(Long catId) {
        TbItemParam tbItemParam = tbItemParamDubboService.selectByCatid(catId);
        if(tbItemParam!=null){
            // 成功时保证data不能为null
            return EgoResult.ok(tbItemParam);
        }
        return EgoResult.error("失败");
    }

    @Override
    public EgoResult insert(TbItemParam tbItemParam) {
        tbItemParam.setId(IDUtils.genItemId());
        Date date = new Date();
        tbItemParam.setCreated(date);
        tbItemParam.setUpdated(date);
        int index = tbItemParamDubboService.insert(tbItemParam);
        if(index==1){
            return EgoResult.ok();
        }
        return EgoResult.error("新增失败");
    }

    @Override
    public EgoResult delete(long[] ids) {
        try {
            int index = tbItemParamDubboService.delete(ids);
            if(index==1){
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("删除失败");
    }
}
