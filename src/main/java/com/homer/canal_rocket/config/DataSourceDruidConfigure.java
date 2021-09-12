package com.homer.canal_rocket.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wangl
 * @Date 2021/9/7 16:44
 * @Version 1.0
 */
@Configuration
public class DataSourceDruidConfigure {
  @ConfigurationProperties(prefix = "spring.datasource")
  @Bean
  public DruidDataSource getDataSour() {
    return new DruidDataSource();
  }

//  @ConfigurationProperties(prefix = "spring.datasource")
//  @Bean
//  public HikariDataSource hikariDataSource() {
//    return new HikariDataSource();
//  }

  //配置Druid的监控
  //1、配置一个管理后台的Servlet
  @Bean
  public ServletRegistrationBean statViewServlet(){
    ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    Map<String,String> initParams = new HashMap<>();

    initParams.put("loginUsername","admin");//登录用户名
    initParams.put("loginPassword","123456");//登录密码
    initParams.put("allow","");//默认就是允许所有访问
    initParams.put("deny","192.168.15.21");//拒绝访问

    bean.setInitParameters(initParams);
    return bean;
  }


  //2、配置一个web监控的filter
  @Bean
  public FilterRegistrationBean webStatFilter(){
    FilterRegistrationBean bean = new FilterRegistrationBean();
    bean.setFilter(new WebStatFilter());
    Map<String,String> initParams = new HashMap<>();
    initParams.put("exclusions","*.js,*.css,/druid/*");
    bean.setInitParameters(initParams);
    bean.setUrlPatterns(Arrays.asList("/*"));

    return  bean;
  }
}

