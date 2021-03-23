package com.quantil.cm.feedback.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import com.quantil.cm.feedback.dto.AlertData;
import com.quantil.cm.feedback.properties.AlertProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Service
public class AlertService {

    private static Logger logger = LoggerFactory.getLogger(AlertService.class);

    @Autowired
    AlertProperties alertProperties;

    @PostConstruct
    public void init() throws UnknownHostException {
        logger.info("alert config:{}", JSON.toJSONString(alertProperties));
        if (alertProperties.getEndpoint() == null) {
            alertProperties.setEndpoint(InetAddress.getLocalHost().getHostName());
        }
    }

    public void alert(AlertData data) {
        alert(Arrays.asList(data));
    }

    public void alert(List<AlertData> data) {
        for (AlertData d : data) {
            if (StringUtils.isBlank(d.getEndpoint())) {
                d.setEndpoint(alertProperties.getEndpoint());
            }
            if (d.getTimestamp() <= 0) {
                d.setTimestamp(System.currentTimeMillis());
            }
        }
        String json = JSON.toJSONString(data);
        logger.error("trigger alert: {}", json);
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(alertProperties.getConnectTimeout())
                        .setSocketTimeout(alertProperties.getSocketTimeout())
                        .build())
                .build();
        HttpPost httpPost = new HttpPost(alertProperties.getAddress());
        httpPost.setEntity(new StringEntity(JSON.toJSONString(data),"UTF-8"));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode/100 != 2) {
                String body = EntityUtils.toString(response.getEntity());
                logger.error("alert return non-2xx status code: {}. body:\n{}",statusCode,body);
            }
            response.close();
        } catch (Throwable t) {
            logger.error("alert failed",t);
        } finally {
            IOUtils.close(response);
            IOUtils.close(httpClient);
        }
    }

}
