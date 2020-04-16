package jp.co.canon.cks.eec.fs.rssportal.connect.postgresql;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.ArrayTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PostgresSqlSessionFactory {

    private final PostgresDataSource dataSource;
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public PostgresSqlSessionFactory(PostgresDataSource dataSource) {
        this.dataSource = dataSource;

        try {
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource.getDataSource());
            factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:mapper/**/*.xml"));
            factoryBean.setTypeAliases(
                    UserPermissionVo.class,
                    UserVo.class);
            /*factoryBean.setTypeHandlers(
                    new ArrayTypeHandler()
            );*/

            sqlSessionFactory = factoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
