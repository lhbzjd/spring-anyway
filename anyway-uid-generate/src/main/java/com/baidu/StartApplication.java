package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName: ImageCenterApplication
 * @Description: TODO
 * @author 李海博 (haibo_li@neusoft.com)
 * @date 2016-11-23 上午10:16:13
 * @version V1.0
 */
@EnableTransactionManagement
@SpringBootApplication(exclude={JpaRepositoriesAutoConfiguration.class})
@EnableDiscoveryClient
@EnableScheduling
public class StartApplication {

    public static void main( String[] args )
    {
        SpringApplication.run(StartApplication.class, args);
    }

}
