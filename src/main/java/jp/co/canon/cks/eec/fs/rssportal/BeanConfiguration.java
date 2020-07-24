package jp.co.canon.cks.eec.fs.rssportal;

import jp.co.canon.ckbs.eec.fs.manage.DefaultFileServiceManageConnectorFactory;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public FileServiceManageConnectorFactory getFileServiceManageConnectorFactory(){
        return new DefaultFileServiceManageConnectorFactory();
    }
}
