package com.ego.cart.service;

import com.ego.cart.pojo.OrderCartPojo;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import org.omg.CORBA.LongHolder;

import java.util.List;

public interface CartService {
    /**
     * 添加购物车
     * @param id
     * @param num
     */
    void addCart(Long id ,int num);

    /**
     * 显示cookie中的商品
     * @return
     */
    List<CartPojo> showCart();

    /**
     * 修改商品数量
     * @param id
     * @param num
     * @return
     */
    EgoResult updateNum(Long id,int num);

    /**
     * 删除商品
     * @param id
     * @return
     */
    EgoResult deleteCart(Long id);

    /**
     * 展示商品总额
     * @param ids
     * @return
     */
    List<OrderCartPojo> showOrderCart(List<Long> ids);
}
