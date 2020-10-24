package com.ego.reciver;

import com.ego.commons.pojo.BigAd;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbContent;
import com.ego.pojo.TbItem;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueueListener {

    @Value("${ego.bigad.categoryId}")
    private Long bidAdId;

    @Value("${ego.search.insert}")
    private String searchInsertUrl;

    @Value("${ego.search.update}")
    private String searchDeleteUrl;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbContentDubboService tbContentDubboService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "${ego.rabbitmq.content.queueName}" ,autoDelete = "false"),
                    exchange = @Exchange(name = "amq.direct",type = "direct" ,autoDelete = "false")
            )
    })
    public void content(Object obj){

        System.out.println("接受到消息： " + obj);
        // redis 数据同步   1、从mysql取出数据把数据缓存到redis中；
        List<TbContent> tbContents = tbContentDubboService.selectByCategoryId(bidAdId);
        List<BigAd> result = new ArrayList<>();
        for (TbContent tb : tbContents) {
            BigAd bigAd = new BigAd();
            bigAd.setAlt("");
            bigAd.setHeight(240);
            bigAd.setHeightB(240);
            bigAd.setWidth(670);
            bigAd.setWidthB(550);
            bigAd.setSrc(tb.getPic());
            bigAd.setSrcB(tb.getPic2());
            bigAd.setHref(tb.getUrl());
            result.add(bigAd);
        }
       redisTemplate.opsForValue().set("com.ego.portal::bigad", JsonUtils.objectToJson(result));
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "${ego.rabbitmq.item.insertName}" ,autoDelete = "false"),
                    exchange = @Exchange(name = "amq.direct",type = "direct" ,autoDelete = "false")
            )
    })
    public void insertItem(String id){
        /**
         * 对solr 同步
         */
        Map<String,String> param = new HashMap<>();
        param.put("id",id+"");
        HttpClientUtil.doPost(searchInsertUrl,param);
        String[] ids = id.split(",");
        /**
         * 对redis 同步
         */
        for (String i: ids) {
            String key = "com.ego.item::details:" + i;
            TbItem tbItem = tbItemDubboService.selectById(Long.parseLong(i));
            TbItemDetails details = new TbItemDetails();
            details.setId(tbItem.getId());
            details.setPrice(tbItem.getPrice());
            details.setSellPoint(tbItem.getSellPoint());
            details.setTitle(tbItem.getTitle());
            String img = tbItem.getImage();
            details.setImages(img!=null && !img.equals("")?img.split(","):new String[1]);
            redisTemplate.opsForValue().set(key,details);
        }

    }


    /**
     * 删除同步
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "${ego.rabbitmq.item.updateName}" ,autoDelete = "false"),
                    exchange = @Exchange(name = "amq.direct",type = "direct" ,autoDelete = "false")
            )
    })
    public void deleteItem(String id){
        Map<String,String> param = new HashMap<>();
        param.put("id",id+"");
        HttpClientUtil.doPost(searchDeleteUrl,param);
        String[] ids = id.split(",");
        for (String i: ids) {
            redisTemplate.delete("com.ego.item::details:"+i);
        }
    }

}
