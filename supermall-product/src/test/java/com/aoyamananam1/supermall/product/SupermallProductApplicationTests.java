package com.aoyamananam1.supermall.product;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.aoyamananam1.supermall.product.entity.BrandEntity;
import com.aoyamananam1.supermall.product.service.BrandService;
import com.aoyamananam1.supermall.product.service.impl.BrandServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.awt.event.ItemEvent;
import java.util.List;

@SpringBootTest()
class SupermallProductApplicationTests {


	@Autowired
	private BrandService brandService;

//	@TestConfiguration
//	static class brandServiceImplTestContextConfiguration{
//
//		@Bean
//		public BrandService brandService(){
//			return new BrandServiceImpl();
//		}
//	}


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

	@Test
	public void testUpload(){
		//构造一个带指定 Region 对象的配置类
		Configuration cfg = new Configuration(Region.region2());
		cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
		//...其他参数参考类注释

		UploadManager uploadManager = new UploadManager(cfg);
		//...生成上传凭证，然后准备上传
		String accessKey = "qOt6AX1zl7RHdsRVgObFH96lI_jYOZb6h6gvAsDd";
		String secretKey = "gus8_iRyEvBJ09yi_yJraqF0X3Ek5fRhUQI3MRHU";
		String bucket = "super-mall-bucket";
		//如果是Windows情况下，格式是 D:\\qiniu\\test.png
		String localFilePath = "D:\\Data\\JavaProj\\sky-take-out-main\\资料\\day03\\图片资源\\2.png";
		//默认不指定key的情况下，以文件内容的hash值作为文件名
//		String key = null;
		String key = "icon/2.png";


		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);

		try {
			Response response = uploadManager.put(localFilePath, key, upToken);
			//解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			System.out.println(putRet.key);
			System.out.println(putRet.hash);
		} catch (QiniuException ex) {
			ex.printStackTrace();
			if (ex.response != null) {
				System.err.println(ex.response);

				try {
					String body = ex.response.toString();
					System.err.println(body);
				} catch (Exception ignored) {
				}
			}
		}

	}

}
