package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.DefaultFileServiceCollectConnectorFactory;
import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnectorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public FileServiceCollectConnectorFactory getFileServiceCollectorFactory(){
        return new DefaultFileServiceCollectConnectorFactory();
    }
}
