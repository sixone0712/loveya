package jp.co.canon.ckbs.eec.fs.collect;

import jp.co.canon.ckbs.eec.fs.manage.DefaultFileServiceManageConnector;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class DefaultFileServiceCollectConnectorFactory implements FileServiceCollectConnectorFactory{
    RestTemplate restTemplate;

    public DefaultFileServiceCollectConnectorFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(10000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(100)
                .build();
        factory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(factory);
    }

    @Override
    public FileServiceCollectConnector getConnector(String host) {
        return new DefaultFileServiceCollectConnector(host, this.restTemplate);
    }
}
