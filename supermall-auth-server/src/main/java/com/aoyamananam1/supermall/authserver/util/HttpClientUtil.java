package com.aoyamananam1.supermall.authserver.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.aoyamananam1.supermall.authserver.exception.Oauth2Exception;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpClientUtil {

    // GET请求
    public static String get(String url, JSONObject params) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String sendUrl = url;
        // 拼接参数
        if (Objects.nonNull(params) && params.size() > 0) {
            sendUrl = connectParams(url, params);
        }
        HttpGet httpGet = new HttpGet(sendUrl);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode() && null != httpEntity) {
                return EntityUtils.toString(httpEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(httpClient, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("调用GET请求失败！");
    }


    /**
     * post请求
     * @param url
     * @param params
     * @param requestBody json串
     * @return
     */
    public static String post(String url, JSONObject params, String requestBody) throws Oauth2Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String sendUrl = url;
        // 1.拼接参数
        if (Objects.nonNull(params) && params.size() > 0) {
            sendUrl = connectParams(url, params);
        }
        HttpPost httpPost = new HttpPost(sendUrl);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        CloseableHttpResponse response = null;
        try {
            // 2.设置request-body
            if (StringUtils.isNotBlank(requestBody)) {
                ByteArrayEntity entity = new ByteArrayEntity(requestBody.getBytes(StandardCharsets.UTF_8));
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode() && null != httpEntity) {
                return EntityUtils.toString(httpEntity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(httpClient, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        throw new RuntimeException("调用POST请求失败！");
        throw new Oauth2Exception();
    }

    private static String connectParams(String url, JSONObject params) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(url).append("?");
        params.forEach((x, y) -> buffer.append(x).append("=").append(y).append("&"));
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public static void close(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) throws IOException{
        if (null != httpClient) {
            httpClient.close();
        }
        if (null != httpResponse) {
            httpResponse.close();
        }
    }
}
