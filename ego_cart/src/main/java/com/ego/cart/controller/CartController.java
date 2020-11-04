package com.ego.cart.controller;

import com.ego.cart.service.CartService;
import com.ego.commons.pojo.CartPojo;
import com.ego.commons.pojo.EgoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/cart/add/{id}.html")
    public String addCart(@PathVariable Long id,int num){
        cartService.addCart(id, num);
        return "cartSuccess";
    }

    @RequestMapping("/cart/cart.html")
    public String showCart(Model model){
        List<CartPojo> list = cartService.showCart();
        model.addAttribute("cartList",list);
        return "cart";
    }

    @RequestMapping(value = {"/cart/update/num/{id}/{num}.action","/service/cart/update/num/{id}/{num}"})
    public EgoResult updateNum(@PathVariable Long id ,@PathVariable int num){
        return cartService.updateNum(id, num);
    }

    @RequestMapping("cart/delete/{id}.action")
    @ResponseBody
    public EgoResult deleteCart(@PathVariable Long id){
        return cartService.deleteCart(id);
    }

    @RequestMapping("/cart/order-cart.html")
    public String showOrderCart(@RequestParam("id") List<Long> id,Model model){
        model.addAttribute("cartList",cartService.showOrderCart(id));
        return "order-cart";
    }
}
