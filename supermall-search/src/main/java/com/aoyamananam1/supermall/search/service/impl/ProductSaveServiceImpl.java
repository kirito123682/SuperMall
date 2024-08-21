package com.aoyamananam1.supermall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.aoyamananam1.supermall.search.constant.EsConstant;
import com.aoyamananam1.supermall.search.service.ProductSaveService;
import com.aoyamananam1.to.es.SkuEsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    ElasticsearchClient esClient;

    @Autowired
    BulkListener<String> bulkListener;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        //保存到es
        // 1 在es中建立索引 product 映射关系

        // 2 在ex中保存数据

//        BulkIngester<String> ingester = BulkIngester.of(b -> b
//                .client(esClient)
//                .maxOperations(100)
//                .flushInterval(1, TimeUnit.SECONDS)
//                .listener(bulkListener)
//        );
//        for (SkuEsModel skuEsModel : skuEsModels) {
//            ingester.add(op -> op
//                    .index(idx -> idx
//                            .index(EsConstant.PRODUCT_INDEX)
//                            .id(skuEsModel.getSkuId().toString())
//                            .document(skuEsModel)
//                    ),
//                    skuEsModel.getSkuTitle()
//            );
//        }
//
//        ingester.close();

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SkuEsModel model : skuEsModels) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(EsConstant.PRODUCT_INDEX)
                            .id(model.getSkuId().toString())
                            .document(model)
                    )
            );
        }
        BulkResponse response = esClient.bulk(br.build());

        //处理异常
        boolean errors = response.errors();
        List<BulkResponseItem> items = response.items();
        if (errors){
            List<ErrorCause> collect = items.stream().map(item -> item.error()).collect(Collectors.toList());
            log.error("商品上架错误： {}", collect);
        }

        return errors;

    }
}
