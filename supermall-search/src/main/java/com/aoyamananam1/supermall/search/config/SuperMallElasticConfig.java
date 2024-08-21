package com.aoyamananam1.supermall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class SuperMallElasticConfig {

    private ElasticsearchTransport transport;

    @Bean
    public BulkListener<String> bulkListener(){
        BulkListener<String> listener = new BulkListener<>(){

            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest, List<String> list) {
                log.debug("before bulk...{}", bulkRequest);
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, List<String> list, BulkResponse bulkResponse) {
                log.debug("bulk requset: {} completed", l);
                List<BulkResponseItem> items = bulkResponse.items();
                for (int i = 0; i < list.size(); i++) {
                    BulkResponseItem item = items.get(i);
                    if (item.error()!= null){
                        log.error("failed to index: {} - {}", list.get(i), item.error().reason());
                    }
                }
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, List<String> list, Throwable throwable) {
                log.error("bulk request : {} failed", l);
            }
        };
        return listener;
    }

    @Bean
    public ElasticsearchClient esRestClient() throws IOException {
        // URL and API key
        String fingerprint = "7268ee03b392d7e43df0d099d71e1833d32df6d5a9dad13c5e0763fbded5dcbe";
        String serverUrl = "https://localhost:9200";
        String host = "127.0.0.1";
        Integer port = 9200;
        String login = "elastic";
//        String serverUrl = "https://172.18.0.2:9200";
        String apiKey = "mnkl+ZsW34FgiT3sTxUv";

        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(login, apiKey));



//        // Create the low-level client
//        RestClient restClient = RestClient
//                .builder(HttpHost.create(serverUrl))
//                .setDefaultHeaders(new Header[]{
//                        new BasicHeader("Authorization", "ApiKey " + apiKey)
//                })
//                .build();

        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                ).build();

        // Create the transport with a Jackson mapper
        this.transport= new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        // Use the client...
        // Close the transport, freeing the underlying thread
//        transport.close();

        return esClient;
    }

    @PreDestroy
    public void closeTransport() throws IOException {
        if (transport != null){
            transport.close();
        }
    }
}
