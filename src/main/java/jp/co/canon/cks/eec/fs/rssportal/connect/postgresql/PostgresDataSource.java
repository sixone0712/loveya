package jp.co.canon.cks.eec.fs.rssportal.connect.postgresql;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class PostgresDataSource {

    private DataSource dataSource;

    public PostgresDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://10.1.31.237:5432/rssdb")
                .username("rssadmin")
                .password("1234");
        dataSource = builder.build();
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
