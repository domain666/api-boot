package org.minbox.framework.api.boot.autoconfigure.datasource;

import org.minbox.framework.api.boot.plugin.datasource.ApiBootDataSource;
import org.minbox.framework.api.boot.plugin.datasource.ApiBootDataSourceFactoryBean;
import org.minbox.framework.api.boot.plugin.datasource.aop.interceptor.ApiBootDataSourceSwitchAnnotationInterceptor;
import org.minbox.framework.api.boot.plugin.datasource.aop.advistor.ApiBootDataSourceSwitchAdvisor;
import org.minbox.framework.api.boot.plugin.datasource.config.DataSourceConfig;
import org.minbox.framework.api.boot.plugin.datasource.routing.ApiBootRoutingDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ApiBoot DataSource Switch AutoConfiguration
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-04-01 22:06
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengboy
 */
@Configuration
@ConditionalOnClass(ApiBootDataSource.class)
@EnableConfigurationProperties(ApiBootDataSourceSwitchProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class ApiBootDataSourceSwitchAutoConfiguration {
    /**
     * ApiBoot DataSource Switch Properties
     */
    private ApiBootDataSourceSwitchProperties apiBootDataSourceSwitchProperties;

    public ApiBootDataSourceSwitchAutoConfiguration(ApiBootDataSourceSwitchProperties apiBootDataSourceSwitchProperties) {
        this.apiBootDataSourceSwitchProperties = apiBootDataSourceSwitchProperties;
    }

    /**
     * ApiBoot DataSource FactoryBean
     * Used to create datasource
     *
     * @return ApiBootDataSourceFactoryBean
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiBootDataSourceFactoryBean apiBootDataSourceFactoryBean() {
        return new ApiBootDataSourceFactoryBean();
    }

    /**
     * ApiBoot Routing DataSource
     * switch use datasource
     *
     * @param apiBootDataSourceFactoryBean ApiBoot DataSource FactoryBean
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(ApiBootDataSourceFactoryBean apiBootDataSourceFactoryBean) {
        List<DataSourceConfig> dataSourceConfigList = new LinkedList();
        Map<String, DataSourceConfig> dataSourceConfigMap = new HashMap(1);

        // put druid datasource config to map
        dataSourceConfigMap.putAll(apiBootDataSourceSwitchProperties.getDruid());
        // put hikari datasource config to map
        dataSourceConfigMap.putAll(apiBootDataSourceSwitchProperties.getHikari());

        // convert all datasource config
        dataSourceConfigMap.keySet().stream().forEach(druidPoolName -> {
            DataSourceConfig dataSourceConfig = dataSourceConfigMap.get(druidPoolName);
            // set data source pool name
            dataSourceConfig.setPoolName(druidPoolName);
            // after convert add to data source list
            dataSourceConfigList.add(dataSourceConfig);
        });

        return new ApiBootRoutingDataSource(apiBootDataSourceFactoryBean, apiBootDataSourceSwitchProperties.getPrimary(), dataSourceConfigList);
    }

    /**
     * ApiBoot DataSource Switch Advice Interceptor
     *
     * @return ApiBootDataSourceSwitchAnnotationInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiBootDataSourceSwitchAnnotationInterceptor apiBootDataSourceSwitchAnnotationInterceptor() {
        return new ApiBootDataSourceSwitchAnnotationInterceptor();
    }

    /**
     * ApiBoot DataSource Switch Advisor
     * Used to get @DataSourceSwitch annotation define
     *
     * @return ApiBootDataSourceSwitchAdvisor
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiBootDataSourceSwitchAdvisor apiBootDataSourceSwitchAdvisor(ApiBootDataSourceSwitchAnnotationInterceptor apiBootDataSourceSwitchAnnotationInterceptor) {
        return new ApiBootDataSourceSwitchAdvisor(apiBootDataSourceSwitchAnnotationInterceptor);
    }

}
