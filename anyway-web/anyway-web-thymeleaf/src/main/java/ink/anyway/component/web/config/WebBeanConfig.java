package ink.anyway.component.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import ink.anyway.component.common.plugin.CustomConfigPlugin;
import ink.anyway.component.common.pojo.DecoratorPath;
import ink.anyway.component.web.bundle.PlugTagRuleBundle;
import ink.anyway.component.web.filter.EmptyFilter;
import ink.anyway.component.web.filter.SessionStatusFilter;
import ink.anyway.component.web.filter.SiteMeshFilter;
import org.sitemesh.builder.SiteMeshFilterBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 16-11-25 下午4:56
 * <br>@version : 1.0
 */
@Configuration
public class WebBeanConfig {

    @Autowired
    private CustomConfigPlugin customConfigPlugin;

    @Bean
    @Primary
    public FilterRegistrationBean siteMeshFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();

        if(customConfigPlugin.getSiteMeshUrlPatterns()==null||customConfigPlugin.getSiteMeshUrlPatterns().length==0){
            registration.setFilter(new EmptyFilter());
            registration.addUrlPatterns("/empty/filter");
            return registration;
        }

        registration.setFilter(new SiteMeshFilter((SiteMeshFilterBuilder builder)->{

            for(DecoratorPath dp:customConfigPlugin.getSiteMeshDecoratorPaths()){
                builder.addDecoratorPath(dp.getContentPath(), dp.getDecoratorPath());
            }

            for(String ep:customConfigPlugin.getSiteMeshExcludedPaths()){
                builder.addExcludedPath(ep);
            }

            builder.addTagRuleBundles(new PlugTagRuleBundle());
        }));
        registration.addUrlPatterns(customConfigPlugin.getSiteMeshUrlPatterns());
        registration.setOrder(100);/**  必须放在[security过滤器链(默认为10个过滤器：1.SecurityContextPersistenceFilter,2.WebAsyncManagerIntegrationFilter...)]之后，否则html中的权限标签无法使用 */
        return registration;
    }

    @Bean
    public FilterRegistrationBean sessionStatusFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SessionStatusFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

}
