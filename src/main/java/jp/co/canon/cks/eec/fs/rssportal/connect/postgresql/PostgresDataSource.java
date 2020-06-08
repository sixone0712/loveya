package jp.co.canon.cks.eec.fs.rssportal.connect.postgresql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class PostgresDataSource {

    private DataSource dataSource;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public DataSource createPostgresDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password);
        dataSource = builder.build();
        return dataSource;
    }

    /*
    public PostgresDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName("org.postgresql.Driver")
                // CKBS [set ip address]
                .url("jdbc:postgresql://localhost:5432/rssdb")
                .username("rssadmin")
                .password("1234");
        dataSource = builder.build();
    }
    */

    public DataSource getDataSource() {
        if(dataSource == null) {
            dataSource = createPostgresDataSource();
        }
        return dataSource;
    }
}
