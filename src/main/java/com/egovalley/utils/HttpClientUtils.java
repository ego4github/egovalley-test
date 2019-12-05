package com.egovalley.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final CloseableHttpClient httpClient;
    private static final CloseableHttpClient httpClientProxy;
    private static final String CHARSET = "UTF-8";

    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .build();
        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        RequestConfig requestConfigProxy = RequestConfig.custom()
                .setProxy(new HttpHost("30.5.109.7", 8081))// 设置代理IP
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .build();
        httpClientProxy = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfigProxy)
                .build();
    }

    /**
     * GET调用
     *
     * @param url    调用URL
     * @param params 调用参数
     * @return String
     */
    public static String sendGetRequest(String url, Map<String, String> params) {
        String result = "";
        if (StringUtils.isBlank(url) || "null".equals(url)) {
            logger.info(">>> get 调用 url 为空");
            return result;
        }
        if (CollectionUtils.isEmpty(params)) {
            logger.info(">>> get 调用 params 为空");
            return result;
        } else {
            try {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
                logger.info(">>> get 调用 url = " + url + "; params = " + params);
                HttpGet get = new HttpGet(url);
                CloseableHttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    get.abort();
                    throw new RuntimeException(">>> get 调用错误码 statusCode = " + statusCode);
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
                response.close();
                logger.info(">>> get 调用结果 result = " + result);
            } catch (IOException e) {
                logger.error(">>> get 调用异常", e);
            }
            return result;
        }
    }

    /**
     * POST调用
     *
     * @param url       调用URL
     * @param params    调用参数
     * @param proxyFlag 是否代理
     * @return JSONString
     */
    public static String sendPostRequestByJson(String url, Map<String, Object> params, boolean proxyFlag) {
        String result = "";
        if (StringUtils.isBlank(url) || "null".equals(url)) {
            logger.info(">>> post 调用 url 为空");
            return result;
        }
        if (CollectionUtils.isEmpty(params)) {
            logger.info(">>> post 调用 params 为空");
            return result;
        } else {
            try {
                logger.info(">>> post 调用 url = " + url + "; proxyFlag = " + proxyFlag + "; params = " + params);
                HttpPost post = new HttpPost(url);
                post.setHeader("Content-type", "application/json");
                String json = JSON.toJSONString(params);
                StringEntity postingString = new StringEntity(json, "application/json", "UTF-8");
                post.setEntity(postingString);
                CloseableHttpResponse response;
                if (proxyFlag) {
                    response = httpClientProxy.execute(post);
                } else {
                    response = httpClient.execute(post);
                }
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    post.abort();
                    throw new RuntimeException(">>> post 调用错误码 statusCode = " + statusCode);
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, CHARSET);
                }
                EntityUtils.consume(entity);
                response.close();
                logger.info(">>> post 调用结果 result = " + result);
            } catch (Exception e) {
                logger.error(">>> post 调用异常", e);
            }
            return result;
        }
    }

}
