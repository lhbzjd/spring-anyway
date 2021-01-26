package ink.anyway.component.common.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import ink.anyway.component.common.engine.ThreadPoolEngine;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 16-11-25 下午4:56
 * <br>@version : 1.0
 */
@Configuration
public class BeanConfig {

    @Bean
    public ConfigurableMapper orikaMapper() {
        return new ConfigurableMapper() {
            @Override
            protected void configure(MapperFactory factory) {
                super.configure(factory);
            }

            @Override
            protected void configureFactoryBuilder(DefaultMapperFactory.Builder factoryBuilder) {
                super.configureFactoryBuilder(factoryBuilder);
            }
        };
    }

    @Bean
    public ThreadPoolEngine threadPoolEngine(){
        return ThreadPoolEngine.createEngine();
    }

    @Bean
    EventBus eventBus(){
        return new EventBus("com.neusoft.micia.event.bus");
    }

    @Bean
    public AsyncEventBus asyncEventBus(ThreadPoolEngine threadPoolEngine){
        return new AsyncEventBus("com.neusoft.micia.async.event.bus", threadPoolEngine.startFixedThreadPool("common.async.event.bus.thread.pool", 100));
    }



}
