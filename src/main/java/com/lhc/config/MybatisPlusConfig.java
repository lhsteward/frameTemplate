package com.lhc.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis-Plus配置类
 * @author lihaisteward
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {



    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("dataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        //全局配置
        GlobalConfig globalConfig  = new GlobalConfig();
        //配置填充器
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        //手动设置session工厂时，需要手动添加分页插件
        Interceptor[] plugins = new Interceptor[1];
        plugins[0] = paginationInterceptor();
        sqlSessionFactoryBean.setPlugins(plugins);
        return sqlSessionFactoryBean.getObject();
    }
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(300);
        paginationInterceptor.setDialectType(DbType.MYSQL.getDb());
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
