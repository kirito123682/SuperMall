package com.aoyamananam1.supermall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aoyamananam1.common.utils.R;
import com.aoyamananam1.supermall.cart.feign.ProductFeignService;
import com.aoyamananam1.supermall.cart.interceptor.CartInterceptor;
import com.aoyamananam1.supermall.cart.service.CartService;
import com.aoyamananam1.supermall.cart.to.SkuInfoTO;
import com.aoyamananam1.supermall.cart.to.UserInfoTO;
import com.aoyamananam1.supermall.cart.vo.Cart;
import com.aoyamananam1.supermall.cart.vo.CartItem;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "supermall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.hasLength(res)){
            //购物车里有该商品
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);

            //加入到redis
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);

            return cartItem;
        }else {
            //购物车里没有该商品
            //添加新商品到购物车
            CartItem cartItem = new CartItem();
            //1 远程查询商品信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoTO skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoTO>() {
                });
                // 添加到购物车
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(skuInfo.getPrice());
            }, executor);
            //3 远程查询sku属性组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            //加入到redis
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);

            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String s = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(s, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();

        if (userInfoTO.getUserId() != null){
            //登录
            String cartKey = CART_PREFIX + userInfoTO.getUserId();
            //如果临时购物车里的数据还没有合并
            String tempCartKey = CART_PREFIX + userInfoTO.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null){
                //临时购物车不为空，需要合并
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                //合并后清除临时购物车数据
                clearCart(tempCartKey);
            }

            //获取登陆后购物车数据（包含合并后的临时购物车数据）
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            //没登录
            String cartKey = CART_PREFIX + userInfoTO.getUserKey();
            //获取临时购物车所有购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        }

        return cart;
    }


    /**
     * 获取到我们要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();

        String cartKey = "";
        if (userInfoTO.getUserId() != null){
            cartKey = CART_PREFIX + userInfoTO.getUserId();
        }else {
            cartKey  = CART_PREFIX + userInfoTO.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && !values.isEmpty()){
            List<CartItem> cartItemList = values.stream().map(obj -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItem.class);
            }).toList();
            return cartItemList;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);

        String s = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }
}
