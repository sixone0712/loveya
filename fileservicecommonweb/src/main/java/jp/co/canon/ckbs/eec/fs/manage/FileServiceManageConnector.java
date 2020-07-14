package jp.co.canon.ckbs.eec.fs.manage;

import org.springframework.web.client.RestTemplate;

public class FileServiceManageConnector {
    RestTemplate restTemplate;
    String host;
    String prefix;

    public FileServiceManageConnector(String host){
        this.restTemplate = new RestTemplate();
        this.host = host;
        this.prefix = String.format("http://%s", this.host);
    }


}
