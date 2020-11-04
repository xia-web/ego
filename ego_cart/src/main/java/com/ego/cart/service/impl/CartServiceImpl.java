package com.ego.cart.service.impl;

import com.ego.cart.pojo.OrderCartPojo;
import com.ego.cart.service.CartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.commons.utils.ServletUtil;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbUser;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    // redis 依赖
    private RedisTemplate<String, Object> redisTemplate;

    @Reference
    private TbItemDubboService tbItemDubboService;
    @Value("${ego.item.details.rediskey}")
    private String detailsKey;
    @Value("${ego.cart.tempcart}")
    private String tempCartKey;
    @Value("${ego.cart.rediskey}")
    private String cartRedisKey;

    @Override
    public void addCart(Long id, int num) {
        // 判断用户是否登录
        TbUser tbUser = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if (tbUser != null) {
            // 存储数据到redis中
            List<CartPojo> list = new ArrayList<>();
            String key = cartRedisKey + tbUser.getId();
            // 第一次向redis中存储数据
            if (redisTemplate.hasKey(key)) {
                list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
                // 判断当前商品是否存在
                for (CartPojo c : list) {
                    if (c.getId().equals(id)) {
                        // 存在则修改数量
                        c.setNum(c.getNum() + num);
                        redisTemplate.opsForValue().set(key, list);
                        return;
                    }
                }
            }
            // 第一次向redis中存储数据
            String detailsRedisKey = detailsKey + id;
            TbItemDetails tbItemDetails = (TbItemDetails) redisTemplate.opsForValue().get(detailsRedisKey);
            CartPojo cartPojo = new CartPojo();
            cartPojo.setId(tbItemDetails.getId());
            cartPojo.setImages(tbItemDetails.getImages());
            cartPojo.setNum(num);
            cartPojo.setPrice(tbItemDetails.getPrice());
            cartPojo.setTitle(tbItemDetails.getTitle());
            list.add(cartPojo);
            redisTemplate.opsForValue().set(key, list);
            return;

        }

        Map<Long, CartPojo> tempCart = new HashMap<>();
        // 第一次操作：cookie中没有数据
        String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempCartKey);
        // 判断cookie临时存放商品
        if (Strings.isNotEmpty(cookieValue)) {
            // 判断是否存在当前商品
            tempCart = JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
            if (tempCart.containsKey(id)) {
                // 如果存在，则修改商品数据
                CartPojo cartPojo = tempCart.get(id);
                cartPojo.setNum(cartPojo.getNum() + num);
                CookieUtils.doSetCookieBase64(ServletUtil.getRequest(), ServletUtil.getResponse(), tempCartKey, JsonUtils.objectToJson(tempCart), 2592000);
                return;
            }

        }
        // 创建 CartPojo 购物车商品
        String key = detailsKey + id;
        TbItemDetails tbItemDetails = (TbItemDetails) redisTemplate.opsForValue().get(key);
        CartPojo cartPojo = new CartPojo();
        cartPojo.setId(tbItemDetails.getId());
        cartPojo.setImages(tbItemDetails.getImages());
        cartPojo.setNum(num);
        cartPojo.setPrice(tbItemDetails.getPrice());
        cartPojo.setTitle(tbItemDetails.getTitle());
        // 加入 Map 中
        tempCart.put(id, cartPojo);
        // 放入到临时购物车中
        CookieUtils.doSetCookieBase64(ServletUtil.getRequest(), ServletUtil.getResponse(), tempCartKey, JsonUtils.objectToJson(tempCart), 2592000);


    }

    @Override
    public List<CartPojo> showCart() {
        TbUser tbUser = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        if (tbUser != null) {
            String key = cartRedisKey + tbUser.getId();
            List<CartPojo> o = (List<CartPojo>) redisTemplate.opsForValue().get(key);
            return o;
        }


        List<CartPojo> list = new ArrayList<>();
        String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempCartKey);
        if (Strings.isNotEmpty(cookieValue)) {
            Map<Long, CartPojo> map = JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
            for (Long id : map.keySet()) {
                list.add(map.get(id));
            }
        }
        return list;
    }

    @Override
    public EgoResult updateNum(Long id, int num) {
        TbUser tbUser = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        // 用户购物车
        if (tbUser != null) {
            String key = cartRedisKey + tbUser.getId();
            List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
            for (CartPojo cp : list) {
                if (cp.getId().equals(id)) {
                    cp.setNum(num);
                    break;
                }
            }
            redisTemplate.opsForValue().set(key, list);
        } else {

            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempCartKey);
            Map<Long, CartPojo> map = JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
            map.get(id).setNum(num);
            CookieUtils.doSetCookieBase64(ServletUtil.getRequest(), ServletUtil.getResponse(), tempCartKey, JsonUtils.objectToJson(map), 2592000);

        }
        return EgoResult.ok();
    }

    @Override
    public EgoResult deleteCart(Long id) {
        TbUser tbUser = (TbUser) ServletUtil.getRequest().getSession().getAttribute("loginUser");
        // 用户购物车
        if (tbUser != null) {
            String key = cartRedisKey + tbUser.getId();
            List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
            for (CartPojo cp : list) {
                if (cp.getId().equals(id)) {
                    list.remove(cp);
                    break;
                }
            }
            redisTemplate.opsForValue().set(key, list);
        } else {

            String cookieValue = CookieUtils.getCookieValueBase64(ServletUtil.getRequest(), tempCartKey);
            Map<Long, CartPojo> cartPojoMap = JsonUtils.jsonToMap(cookieValue, Long.class, CartPojo.class);
            cartPojoMap.remove(id);
            CookieUtils.doSetCookieBase64(ServletUtil.getRequest(), ServletUtil.getResponse(), tempCartKey, JsonUtils.objectToJson(cartPojoMap), 2592000);
        }
        return EgoResult.ok();
    }

    @Override
    public List<OrderCartPojo> showOrderCart(List<Long> ids) {
        List<OrderCartPojo> listResult= new ArrayList<>();
        TbUser loginUser = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
        String key = cartRedisKey + loginUser.getId();
        List<CartPojo> list = (List<CartPojo>) redisTemplate.opsForValue().get(key);
        for (Long id:ids) {
            for (CartPojo cp : list) {
                if (cp.getId().equals(id)) {
                    OrderCartPojo cartPojo = new OrderCartPojo();
                    BeanUtils.copyProperties(cp,cartPojo);
                    TbItem tbItem = tbItemDubboService.selectById(id);
                    if (tbItem.getNum() >= cp.getNum()){
                        cartPojo.setEnough(true);
                    }else {
                        cartPojo.setEnough(false);
                    }
                    listResult.add(cartPojo);
                    break;
                }

            }
        }

        return listResult;
    }
}
