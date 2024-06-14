package com.ding.ptcg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class PostRequestExample {
    public static void main(String[] args) throws Exception {
        // 创建HttpClient实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HttpPost对象
            HttpPost httpPost = new HttpPost("https://tcg.mik.moe/api/v1/ptcg/card-detail");

            // 设置请求头，例如Content-Type为application/json
            httpPost.setHeader("Content-Type", "application/json");

            // 创建要发送的JSON数据
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("setCode", "CS5aC");
            requestBody.put("cardIndex", "144");

            // 将Map对象转换为JSON字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestBody);

            // 将JSON字符串设置为请求实体
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            // 执行请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 获取响应实体
                HttpEntity responseEntity = response.getEntity();

                // 如果响应实体不为空，则读取响应内容
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    System.out.println("Response: " + responseString);

                    // 如果需要，可以将响应字符串转换为Java对象
                    // MyResponseClass responseObject = objectMapper.readValue(responseString, MyResponseClass.class);
                }
            }
        }
    }
}
