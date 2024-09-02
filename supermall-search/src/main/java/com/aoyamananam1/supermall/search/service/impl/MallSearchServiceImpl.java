package com.aoyamananam1.supermall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.aoyamananam1.supermall.search.constant.EsConstant;
import com.aoyamananam1.supermall.search.feign.ProductFeignService;
import com.aoyamananam1.supermall.search.service.MallSearchService;
import com.aoyamananam1.supermall.search.vo.SearchParamVO;
import com.aoyamananam1.supermall.search.vo.SearchRespVO;
import com.aoyamananam1.to.es.SkuEsModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    ElasticsearchClient esClient;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public SearchRespVO search(SearchParamVO searchParamVO) {
        SearchRespVO searchRespVO = null;

        //准备检索请求
        SearchRequest build = buildSearchRequset(searchParamVO).build();

//        System.out.println(build);
        log.info("发送检索请求： {}", build);

        try {
            SearchResponse<SkuEsModel> response = esClient.search(build, SkuEsModel.class);
//            System.out.println(response);
            searchRespVO = BuildSearchResponse(response, searchParamVO);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return searchRespVO;
    }

    /**
     * 构建返回数据
     *
     * @param response
     * @return
     */
    private SearchRespVO BuildSearchResponse(SearchResponse<SkuEsModel> response, SearchParamVO param) {
        SearchRespVO respVO = new SearchRespVO();


        HitsMetadata<SkuEsModel> hits = response.hits();

        long total = hits.total().value();
        respVO.setTotal(total);//总记录数
        respVO.setPageNum(param.getPageNum());//分页信息
        int totalPage = (int) (total % EsConstant.PRODUCT_PAGESIZE == 0 ? total / EsConstant.PRODUCT_PAGESIZE : total / EsConstant.PRODUCT_PAGESIZE + 1);
        respVO.setTotalPages(totalPage);//总页数
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        respVO.setPageNavs(pageNavs);//可供选择的页码集合

        //如果查询结果为空，直接返回
        if (total == 0) {
            return respVO;
        }

        //商品
        List<SkuEsModel> products = new ArrayList<>();
        hits.hits().forEach(item -> {
            SkuEsModel source = item.source();

            //如果查询了关键字，则需要高亮
            if ((StringUtils.hasLength(param.getKeyword()))) {
                String s = item.highlight().get("skuTitle").get(0);
                source.setSkuTitle(s);
            }
            products.add(source);//商品集合
        });
        respVO.setProducts(products);

        Map<String, Aggregate> aggregations = response.aggregations();
        Aggregate catalogAgg = aggregations.get("catalog_agg");
        Aggregate attrAgg = aggregations.get("attr_agg");
        Aggregate brandAgg = aggregations.get("brand_agg");

        // 分类聚合
        List<SearchRespVO.CatalogVO> catalogs = new ArrayList<>();// 准备分类集合
        List<LongTermsBucket> buckets = catalogAgg.lterms().buckets().array();
        buckets.stream().forEach(item -> {
            SearchRespVO.CatalogVO catalogVO = new SearchRespVO.CatalogVO();
            catalogVO.setCatalogId(item.key());//每个分类的id
            List<StringTermsBucket> catalogNameAgg = item.aggregations().get("catalog_name_agg").sterms().buckets().array();
            catalogNameAgg.forEach(name -> {
                String s = name.key().stringValue();
                catalogVO.setCatalogName(s);//每个分类的name
            });
            //添加到集合中
            catalogs.add(catalogVO);
        });
        respVO.setCatalogs(catalogs);

        // 品牌聚合
        List<SearchRespVO.BrandVO> brands = new ArrayList<>();
        Map<Long, String> brandMap = new HashMap<>();
        brandAgg.lterms().buckets().array().stream().forEach(item -> {
            SearchRespVO.BrandVO brandVO = new SearchRespVO.BrandVO();
            brandVO.setBrandId(item.key());//品牌id
            item.aggregations().get("brand_img_agg").sterms().buckets().array().stream().forEach(img -> {
                brandVO.setBrandImg(img.key().stringValue());//品牌img
            });
            item.aggregations().get("brand_name_agg").sterms().buckets().array().stream().forEach(name -> {
                brandVO.setBrandName(name.key().stringValue());//品牌名称
            });
            brandMap.put(brandVO.getBrandId(), brandVO.getBrandName());
            brands.add(brandVO);
        });
        respVO.setBrands(brands);

        //属性聚合
        Map<String, String> attrMap = new HashMap<>();
        List<SearchRespVO.AttrVO> attrs = new ArrayList<>();
        attrAgg.nested().aggregations().get("attr_id_agg").lterms().buckets().array().stream().forEach(id -> {
            SearchRespVO.AttrVO attrVO = new SearchRespVO.AttrVO();
            attrVO.setAttrId(id.key());//属性id
            id.aggregations().get("attr_name_agg").sterms().buckets().array().stream().forEach(name -> {
                attrVO.setAttrName(name.key().stringValue());//属性名称
            });
            List<String> attrValue = new ArrayList<>();//准备一个属性值集合
            id.aggregations().get("attr_value_agg").sterms().buckets().array().stream().forEach(value -> {
                attrValue.add(value.key().stringValue());
            });
            attrVO.setAttrValue(attrValue);//属性值集合

            //将查到的attrId和name放入map中，后续面包屑导航要用
            attrMap.put(String.valueOf(attrVO.getAttrId()), attrVO.getAttrName());

            //加入属性集合中
            attrs.add(attrVO);
        });
        respVO.setAttrs(attrs);
        respVO.setAttrIds(attrMap.keySet().stream().map(Long::parseLong).toList());

        // 构建面包屑导航
        List<SearchRespVO.NavVo> navs = new ArrayList<>();
        //如果查询了相应属性才需要做
        if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
            List<SearchRespVO.NavVo> attrNavs = param.getAttrs().stream().map(attr -> {
                SearchRespVO.NavVo navVo = new SearchRespVO.NavVo();
                //分析每个attrs传过来的参数值
                String[] split = attr.split("_");
                navVo.setNavValue(split[1]);

                //通过attrId得到name

                //使用feign做
//            R r = productFeignService.getAttrInfo(Long.valueOf(split[0]));
//            if (r.getCode() == 0){
//                r.getData("attr", new TypeReference<AttrRespVO>(){});//只有product服务有
//            }

                //使用先前构建好的map
                String name = attrMap.get(split[0]);
                navVo.setNavName(name);

                //取消了面包屑后的跳转链接
                //拿到所有的查询条件，去掉当前的
                String queryString = httpServletRequest.getQueryString();
                String replace = replaceQueryString(attr, queryString, "attrs");
                navVo.setLink("http://search.supermall00.com/list.html?" + replace);

                return navVo;
            }).toList();
            navs.addAll(attrNavs);
        }

        // 品牌和分类也做面包屑导航
        //品牌不为空时做
        if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
            List<SearchRespVO.NavVo> brandNavs = param.getBrandId().stream().map(id -> {
                SearchRespVO.NavVo navVo = new SearchRespVO.NavVo();
                navVo.setNavName("品牌");
                navVo.setNavValue(brandMap.get(id));

                String queryString = httpServletRequest.getQueryString();
                String s = replaceQueryString(id + "", queryString, "brandId");
                navVo.setLink("http://search.supermall00.com/list.html?" + s);

                return navVo;
            }).toList();
            navs.addAll(brandNavs);
        }

        respVO.setNavs(navs);

        return respVO;
    }

    private static String replaceQueryString(String value, String queryString, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String replace = queryString.replace("&" + key + "=" + encode, "");
        return replace;
    }

    /**
     * 构建请求
     *
     * @return
     */
    private SearchRequest.Builder buildSearchRequset(SearchParamVO param) {
        SearchRequest.Builder sb = new SearchRequest.Builder();
        sb.index(EsConstant.PRODUCT_INDEX);

        //1  查询
        BoolQuery boolQuery = BoolQuery.of(bq -> {
                    //1.1 标题不为空
                    if (StringUtils.hasLength(param.getKeyword())) {
//                        Query skuTitle = MatchQuery.of(m -> m
//                                .field("skuTitle")
//                                .query(param.getKeyword())
//                        )._toQuery();
//                        bq.must(skuTitle);
                        bq.must(m -> m
                                .match(mt -> mt
                                        .field("skuTitle")
                                        .query(param.getKeyword())
                                )
                        );
                    }

                    boolean nullCatalog = param.getCatalog3Id() == null;
                    boolean nullBrandId = param.getBrandId() == null || param.getBrandId().isEmpty();
                    boolean nullAttrs = param.getAttrs() == null || param.getAttrs().isEmpty();
                    boolean nullStock = param.getHasStock() == null;
                    boolean nullPrice = !StringUtils.hasLength(param.getSkuPrice());


                    if (nullCatalog &&
                            nullBrandId &&
                            nullAttrs &&
                            nullStock &&
                            nullPrice) {
                        return bq;
                    }

                    //1.2  filter过滤
                    bq.filter(f -> {

                        //三级分类id不为空
                        if (param.getCatalog3Id() != null) {
                            f.term(t -> t
                                    .field("catalogId")
                                    .value(param.getCatalog3Id())
                            );
                        }

                        //品牌id不为空
                        if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
                            //先构建好list
                            List<FieldValue> list = param.getBrandId().stream().map(FieldValue::of).toList();
                            f.terms(t -> t
                                    .field("brandId")
                                    .terms(TermsQueryField.of(tqf -> tqf
                                            .value(list))
                                    )
                            );
                        }

                        // 按照所有给定的属性
                        if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
                            //遍历
                            for (String attr : param.getAttrs()) {
                                String[] split = attr.split("_");
                                String attrId = split[0];//检索id
                                String[] attrValues = split[1].split(":");//属性值
                                List<FieldValue> fieldValueList = Arrays.stream(attrValues).map(FieldValue::of).toList();
                                //每一个都创造一个nested
                                f.nested(n -> n
                                        .path("attrs")
                                        .query(q ->
                                                q.bool(b -> b
                                                        .must(m -> m
                                                                .term(t -> t
                                                                        .field("attrs.attrId")
                                                                        .value(attrId)
                                                                )
                                                        )
                                                        .must(m -> m
                                                                .terms(t -> t
                                                                        .field("attrs.attrValue")
                                                                        .terms(TermsQueryField.of(tqf -> tqf
                                                                                .value(fieldValueList))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                );
                            }
                        }

                        //是否拥有库存
                        if (param.getHasStock() != null) {
                            f.term(t -> t
                                    .field("hasStock")
                                    .value(param.getHasStock() == 1)
                            );
                        }

                        //价格区间
                        if (StringUtils.hasLength(param.getSkuPrice())) {
                            //先进行分割
                            String[] split = param.getSkuPrice().split("_");

                            f.range(r -> r
                                    .number(n -> {
                                                n.field("skuPrice");
                                                if (split[0] != null && !split[0].isEmpty()) {
                                                    n.gte(Double.valueOf(split[0]));
                                                }
                                                if (split[1] != null && !split[1].isEmpty()) {
                                                    n.lte(Double.valueOf(split[1]));
                                                }
                                                return n;
                                            }
                                    )
                            );
                        }
                        return f;
                    });

                    return bq;
                }
        );

//        //三级分类id不为空
//        if (param.getCatalog3Id() != null) {
//            boolQuery.filter().add(TermQuery.of(t -> t
//                    .field("catalogId")
//                    .value(param.getCatalog3Id()))._toQuery());
//
//
//        }

        sb.query(boolQuery._toQuery());
//        if (StringUtils.hasLength(param.getKeyword())) {
//            Query skuTitle = MatchQuery.of(m -> m
//                    .field("skuTitle")
//                    .query(param.getKeyword())
//            )._toQuery();
//        }
        //2 排序，分页 高亮
        //2.1 排序
        if (StringUtils.hasLength(param.getSort())) {
            String[] split = param.getSort().split("_");
            sb.sort(s -> s
                    .field(f -> f
                            .field(split[0])
                            .order(split[1].equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc))
            );
        }

        //2.2 分页
        sb.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sb.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3 高亮
        if (StringUtils.hasLength(param.getKeyword())) {
            sb.highlight(hl -> hl
                    .fields("skuTitle", f -> f
                            .preTags("<b style='color:red'>")
                            .postTags("</b>")
                    )
            );
        }

        //3 聚合分析

        //品牌聚合
        sb.aggregations("brand_agg", ag -> ag
                        .terms(t -> t
                                .field("brandId")
                                .size(10))
                        //品牌子聚合
                        .aggregations("brand_name_agg", ag2 -> ag2
                                .terms(t -> t
                                        .field("brandName")
                                        .size(1))
                        )
                        .aggregations("brand_img_agg", ag2 -> ag2
                                .terms(t -> t
                                        .field("brandImg")
                                        .size(1))
                        )
                )
                //分类聚合
                .aggregations("catalog_agg", ag -> ag
                        .terms(t -> t
                                .field("catalogId")
                                .size(20))
                        .aggregations("catalog_name_agg", ag2 -> ag2
                                .terms(t -> t
                                        .field("catalogName")
                                        .size(1))
                        )
                )
                //属性聚合
                .aggregations("attr_agg", ag -> ag
                        .nested(n -> n
                                .path("attrs"))
                        .aggregations("attr_id_agg", ag2 -> ag2
                                .terms(t -> t
                                        .field("attrs.attrId"))
                                .aggregations("attr_name_agg", ag3 -> ag3
                                        .terms(t -> t
                                                .field("attrs.attrName")
                                                .size(1))
                                )
                                .aggregations("attr_value_agg", ag3 -> ag3
                                        .terms(t -> t
                                                .field("attrs.attrValue")
                                                .size(50))
                                )
                        )
                );
        return sb;
    }
}
