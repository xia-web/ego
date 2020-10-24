package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
import com.ego.sender.Send;
import com.ego.service.TbItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbItemServiceImpl implements TbItemService {
    @Reference
    private TbItemDubboService tbItemDubboService;

    @Value("${ego.rabbitmq.item.insertName}")
    private  String insertItemQueue;

    @Value("${ego.rabbitmq.item.updateName}")
    private String deleteItemQueue;


    @Autowired
    private Send send;

    @Override
    public EasyUIDatagrid showItem(int page, int rows) {
        List<TbItem> list = tbItemDubboService.selectByPage(rows, page);
        long total = tbItemDubboService.selectCount();
        return new EasyUIDatagrid(list, total);
    }

    @Override
    public EgoResult updateStatus(long[] ids, int status) {
        try {
            int index = tbItemDubboService.updateStatusByIds(ids, status);
            if (index == 1) {
                if (status == 1){
                    //上架，对solr是新增
                    send.send(insertItemQueue, StringUtils.join(ids,','));
                }else if (status ==2 || status ==3){
                    send.send(deleteItemQueue,StringUtils.join(ids,','));
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("操作失败");
    }

    @Override
    public EgoResult insert(TbItem item, String desc,String itemParams) {
        Date date = new Date();
        long id = IDUtils.genItemId();
        // 商品表数据
        item.setCreated(date);
        item.setUpdated(date);
        // 在分布式项目表主键绝大多数都是通过算法生成。尤其后面是使用mycat，分库分表时，必须使用自定义主键值。
        item.setId(id);
        item.setStatus((byte)1);

        // 商品描述表
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(id);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setCreated(date);

        // 商品规格参数
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setId(IDUtils.genItemId());
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setItemId(id);
        tbItemParamItem.setParamData(itemParams);

        try {
            int index = tbItemDubboService.insert(item, tbItemDesc,tbItemParamItem);
            if(index==1){
                // 发送消息
                send.send(insertItemQueue,id);
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("新增失败");
    }

    @Override
    public EgoResult update(TbItem item, String desc,String itemParams,long itemParamId) {
        Date date = new Date();
        item.setUpdated(date);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setItemId(item.getId());

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setId(itemParamId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);

        try {
            int index = tbItemDubboService.update(item, tbItemDesc,tbItemParamItem);
            if(index==1){
                send.send(insertItemQueue,item.getId());
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("修改失败");
    }
}
