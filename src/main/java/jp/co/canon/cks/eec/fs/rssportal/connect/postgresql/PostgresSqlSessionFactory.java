package jp.co.canon.cks.eec.fs.rssportal.connect.postgresql;

import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

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
                    UserVo.class,
                    GenreVo.class,
                    CommandVo.class,
                    CollectPlanVo.class,
                    DownloadListVo.class,
                    DownloadHistoryVo.class);
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
