package jp.co.canon.ckbs.eec.fs.manage;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class DefaultFileServiceManageConnectorFactory implements FileServiceManageConnectorFactory{
    RestTemplate restTemplate;

    public DefaultFileServiceManageConnectorFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(10000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(200)
                .build();
        factory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(factory);
    }

    @Override
    public FileServiceManageConnector getConnector(String host) {
        return new DefaultFileServiceManageConnector(host, restTemplate);
    }
}
