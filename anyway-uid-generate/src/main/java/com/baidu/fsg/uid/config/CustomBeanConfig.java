package com.baidu.fsg.uid.config;

import com.neusoft.micia.common.plugin.CustomConfigPlugin;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 2018/5/31 15:08
 * <br>@version : 1.0
 */
@Configuration
public class CustomBeanConfig {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CustomConfigPlugin customConfigPlugin() {

        return CustomConfigPlugin.builder()



                .build();
    }

}
