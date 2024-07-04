package com.aoyamananam1.supermall.product;

import com.aoyamananam1.supermall.product.entity.BrandEntity;
import com.aoyamananam1.supermall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.event.ItemEvent;
import java.util.List;

@SpringBootTest
class SupermallProductApplicationTests {

	@Autowired
	private BrandService brandService;

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setBrandId(6L);
		brandEntity.setDescript("iphone18");
		brandEntity.setName("APPLE");
//		brandService.save(brandEntity);
		brandService.updateById(brandEntity);
		System.out.println("save success..");
	}

	@Test
	void test(){
		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 6L));
		list.forEach((Item) -> {
			System.out.println(Item);
		});
	}

}
