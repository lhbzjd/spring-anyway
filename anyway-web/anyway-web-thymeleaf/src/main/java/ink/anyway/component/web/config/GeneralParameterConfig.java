package ink.anyway.component.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import ink.anyway.component.web.interceptor.GeneralParameterInterceptor;
import ink.anyway.component.web.supple.jackson.CustomNullStringSerializerProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "remote.static", name = "path")
public class GeneralParameterConfig extends WebMvcConfigurationSupport  /** 继承此类后，springboot默认的static文件夹下为静态资源的约定失效，web访问静态资源时仍会经过DispatcherServlet去分发，从而得不到资源。 */
{
    @Value("${remote.static.path}")
    private String remoteStaticPath = "";

    @Value("${portal.web.title}")
    private String portalWebTitle = "";

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        super.addInterceptors(registry);
        registry.addWebRequestInterceptor(new GeneralParameterInterceptor(remoteStaticPath, portalWebTitle)).addPathPatterns("/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //定义Json转换器
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        //定义对象映射器
        ObjectMapper objectMapper = new ObjectMapper();
        //定义对象模型
        SimpleModule simpleModule = new SimpleModule();
        //添加对长整型的转换关系
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        //将对象模型添加至对象映射器
        objectMapper.registerModule(simpleModule);

        objectMapper.setSerializerProvider(new CustomNullStringSerializerProvider());

        //将对象映射器添加至Json转换器
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //在转换器列表中添加自定义的Json转换器
        converters.add(jackson2HttpMessageConverter);
        //添加utf-8的默认String转换器
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

}
