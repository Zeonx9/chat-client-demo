package com.ade.chatclient.application;

import com.ade.chatclient.ClientApplication;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.http.HttpClient;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Properties;

public class HttpClientFactory {
    private static HttpClient trustingHttpClient = null;
    private static RestTemplate trustingRestTemplate = null;
    public static final SSLContext inTouchOnlySSLContext;

    static {
        Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        try {
            Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(
                    ClientApplication.class.getResourceAsStream("certificate/intouch-certificate.crt")
            );
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("intouch", certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            inTouchOnlySSLContext = SSLContext.getInstance("TLS");
            inTouchOnlySSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
        }
        catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RestTemplate getTrustingRestTemplate() {
        if (trustingRestTemplate == null) {
            SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(
                    inTouchOnlySSLContext,
                    (hostname, session) -> true
            );

            final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslConFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            trustingRestTemplate = new RestTemplate(requestFactory);
        }
        return trustingRestTemplate;
    }

    public static HttpClient getTrustingHttpClient() {
        if (trustingHttpClient == null) {
            trustingHttpClient = HttpClient.newBuilder()
                    .sslContext(inTouchOnlySSLContext)
                    .build();
        }
        return trustingHttpClient;
    }

}
