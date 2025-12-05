package com.codeit.sb06deokhugamteam2.common.config;

import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    private final DataSourceProperties properties;

    @Bean
    public DataSource dataSource() {
        DataSource realDataSource = DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();

        return new ProxyDataSourceBuilder(realDataSource)
                .name("datasource-proxy-logger")
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .logSlowQueryBySlf4j(1, TimeUnit.SECONDS, SLF4JLogLevel.WARN, "datasource-proxy-logger")
                .countQuery()
                .queryTransformer(transformInfo -> {
                    String sql = transformInfo.getQuery();
                    return FormatStyle.BASIC.getFormatter().format(sql);
                })
                .multiline()
                .build();
    }
}
