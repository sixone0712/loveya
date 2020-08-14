package jp.co.canon.ckbs.eec.fs.manage;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class DefaultFileServiceManageConnectorFactory implements FileServiceManageConnectorFactory{
    RestTemplate restTemplate;

    public DefaultFileServiceManageConnectorFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectionRequestTimeout(3000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(5)
                .build();
        factory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(factory);
    }

    @Override
    public FileServiceManageConnector getConnector(String host) {
        return new DefaultFileServiceManageConnector(host, restTemplate);
    }
}
