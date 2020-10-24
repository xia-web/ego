package com.ego.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.commons.pojo.EasyUIDatagrid;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.pojo.TbContent;
import com.ego.sender.Send;
import com.ego.service.TbContentService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbContentServiceImpl implements TbContentService {
    @Reference
    private TbContentDubboService tbContentDubboService;

    @Value("${ego.rabbitmq.content.queueName}")
    private String queueName;

    @Value("${ego.bigad.categoryId}")
    private Long bigadId;

    @Autowired
    private Send send;

    @Override
    public EasyUIDatagrid showContent(Long categoryId, int page, int rows) {
        List<TbContent> list = tbContentDubboService.selectBypage(categoryId, page, rows);
        long total = tbContentDubboService.selectCountByCategoryid(categoryId);
        return new EasyUIDatagrid(list,total);
    }

    @Override
    public EgoResult insert(TbContent tbContent) {
        tbContent.setId(IDUtils.genItemId());
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        int index = tbContentDubboService.insert(tbContent);
        if(index==1){
            if (tbContent.getCategoryId().equals(bigadId)) {
                send.send(queueName, "async");
                System.out.println("发送了消息");
            }
            return EgoResult.ok();
        }
        return EgoResult.error("新增失败");
    }

    @Override
    public EgoResult update(TbContent tbContent) {
        tbContent.setUpdated(new Date());
        int index = tbContentDubboService.update(tbContent);
        if(index==1){
            if(index==1){
                if (tbContent.getCategoryId().equals(bigadId)) {
                    send.send(queueName, "async");
                    System.out.println("发送了消息");
                }
                return EgoResult.ok();
            }
            return EgoResult.ok();
        }
        return EgoResult.error("修改失败");
    }

    @Override
    public EgoResult delete(long[] ids) {
        boolean isBigAd = false;
        for (long id : ids) {
            TbContent tbContent = tbContentDubboService.selectById(id);
            if (tbContent.getCategoryId().equals(bigadId)){
                isBigAd = true;
                break;
            }
        }
        try {
            int index = tbContentDubboService.deleteByIds(ids);
            if(index==1){
                if (isBigAd){
                    send.send(queueName, "async");
                    System.out.println("发送了消息");
                }
                return EgoResult.ok();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return EgoResult.error("删除失败");
    }
}
