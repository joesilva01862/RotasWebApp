package com.javacodegeeks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.javacodegeeks.service.StatService;
import com.javacodegeeks.service.StatServiceImpl;

@EnableWebMvc
@Configuration
@ComponentScan({"com.javacodegeeks.*"})
public class WebConfig extends WebMvcConfigurerAdapter {

//    @Bean
//    public RestTemplate template(){
//        return new RestTemplate();
//    }

//    @Bean
//    public StatService getService(){
//        return new StatServiceImpl();
//    }

}
