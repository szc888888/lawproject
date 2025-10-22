package io.renren.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mybatis Plus 配置
 *
 * @author ruoyi
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
@MapperScan("io.renren.**.dao")
@EnableAutoConfiguration
public class MybatisPlusConfig
{
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        // 阻断插件
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 分页插件，自动识别数据库类型 https://baomidou.com/guide/interceptor-pagination.html
     */
    public PaginationInnerInterceptor paginationInnerInterceptor()
    {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件 https://baomidou.com/guide/interceptor-optimistic-locker.html
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor()
    {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 如果是对全表的删除或更新操作，就会终止该操作 https://baomidou.com/guide/interceptor-block-attack.html
     */
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor()
    {
        return new BlockAttackInnerInterceptor();
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new UpdateMetaObjectHandler());
        return globalConfig;
    }

//    @Bean
//    public SqlSessionFactory db1SqlSessionFactory(DataSource dataSource) throws Exception {
//        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
////        MybatisConfiguration configuration = new MybatisConfiguration();
////
////        bean.setConfiguration(configuration); // 2
//        bean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
//        bean.setDataSource(dataSource);
//        // ******此处******
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setMetaObjectHandler(new UpdateMetaObjectHandler());
//        bean.setGlobalConfig(globalConfig);
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*Mapper.xml"));
//        bean.setTypeAliasesPackage("com.ruoyi.**.domain");
//        //        // 配置
//
//
//        return bean.getObject();
//    }

//    @Bean(name="MybatisSqlSessionFactoryBean")
//    public SqlSessionFactory mybatisSqlSessionFactory(DataSource ds, MybatisPlusInterceptor interceptor) throws Exception{
//        MybatisSqlSessionFactoryBean ms=new MybatisSqlSessionFactoryBean();
//        ms.setPlugins(interceptor);
//        ms.setDataSource(ds);
////        ms.setConfiguration(this.myConfiguration());
//        GlobalConfig globalConfig=new GlobalConfig();
//        globalConfig.setMetaObjectHandler(new UpdateMetaObjectHandler());
//        globalConfig.setBanner(false);
//        ms.setGlobalConfig(globalConfig);
//        return ms.getObject();
//    }

}

