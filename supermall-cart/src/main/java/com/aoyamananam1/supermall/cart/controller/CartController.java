package com.aoyamananam1.supermall.cart.controller;

import com.aoyamananam1.common.constant.AuthServerConstant;
import com.aoyamananam1.supermall.cart.interceptor.CartInterceptor;
import com.aoyamananam1.supermall.cart.service.CartService;
import com.aoyamananam1.supermall.cart.to.UserInfoTO;
import com.aoyamananam1.supermall.cart.vo.Cart;
import com.aoyamananam1.supermall.cart.vo.CartItem;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    CartService cartService;

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);

        return "redirect:http://cart.supermall00.com/addToCartSuccess.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);

        return "redirect:http://cart.supermall00.com/addToCartSuccess.html";
    }


    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);

        return "redirect:http://cart.supermall00.com/addToCartSuccess.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
//        model.addAttribute("cartItem", cartItem);
//        model.addAttribute("skuId", skuId);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.supermall00.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        //重定向到成功页面，再次查询购物车数据
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);

        return "success";
    }
}
