package com.aoyamananam1.supermall.product.web;

import com.aoyamananam1.supermall.product.entity.CategoryEntity;
import com.aoyamananam1.supermall.product.service.CategoryService;
import com.aoyamananam1.supermall.product.vo.Catelog2VO;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Resource
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){

        //TODO 查出所有1级分类
        List<CategoryEntity> entities = categoryService.getLevelOneCategorys();

        model.addAttribute("categorys",entities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2VO>> getCatalogJson(){
        Map<String, List<Catelog2VO>> map = categoryService.getCatalogJson();

        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1 获取一把锁， 只要名字一样就是同一把锁
        RLock lock = redisson.getLock("my-lock");

        //2 加锁
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            //3 解锁
            System.out.println("释放锁.." + Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }
}
