package jp.co.canon.cks.eec.fs.rssportal;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@SpringBootApplication
public class RssportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssportalApplication.class, args);
    }

//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource ds) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(ds);
//        factory.setMapperLocations(
//                new PathMatchingResourcePatternResolver().getResource("classpath*:sql/**/*.xml")
//        );
//        return factory.getObject();
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
//        return new SqlSessionTemplate(factory);
//    }
}
