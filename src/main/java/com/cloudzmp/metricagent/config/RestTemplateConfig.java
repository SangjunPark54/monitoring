package com.cloudzmp.metricagent.config;

import java.security.cert.X509Certificate;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RestTemplateConfig {

    public RestTemplate createRestTemplate() throws Exception {
        // SSLContext 설정
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return null; }
        }}, new java.security.SecureRandom());

        // HttpsURLConnection의 기본 SSL 설정 변경
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // RestTemplate 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // HttpsURLConnection의 HostnameVerifier 설정
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        return new RestTemplate(factory);
    }
}
