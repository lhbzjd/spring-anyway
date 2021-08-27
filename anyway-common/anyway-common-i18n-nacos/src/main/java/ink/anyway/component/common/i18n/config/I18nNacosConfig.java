package ink.anyway.component.common.i18n.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import ink.anyway.component.common.properties.I18nMessagesProperties;
import ink.anyway.component.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Executor;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.anyway.messages", name = "basename")
public class I18nNacosConfig {

    private String serverAddr;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private I18nMessagesProperties i18nMessagesProperties;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private String i18nNamespace = "";
    private String i18nGroup = "i18n";

    @PostConstruct
    public void init() {
        serverAddr = applicationContext.getEnvironment().getProperty("spring.cloud.nacos.server-addr");
        i18nNamespace = this.i18nMessagesProperties.getNacosNamespace();
        if (StringUtil.isValid(i18nNamespace)) {
            i18nNamespace = i18nNamespace.trim();
        }else{
            i18nNamespace = "";
        }

        i18nGroup = this.i18nMessagesProperties.getNacosGroup();
        if (StringUtil.isValid(i18nGroup)) {
            i18nGroup = i18nGroup.trim();
        }else {
            i18nGroup = "i18n";
        }

        initTip(null);
        if(i18nMessagesProperties.getLocaleCollection()!=null&&i18nMessagesProperties.getLocaleCollection().size()>0){
            for(String locale : i18nMessagesProperties.getLocaleCollection()){
                if(StringUtil.isValid(locale)){
                    String[] localeArr = locale.trim().split("_");
                    if(localeArr!=null){
                        List<String> localeList = new ArrayList<>();
                        for(String mo:localeArr){
                            if(StringUtil.isValid(mo))
                                localeList.add(mo.trim());
                        }

                        if(localeList.size()==1)
                            initTip(new Locale(localeList.get(0)));
                        if(localeList.size()==2)
                            initTip(new Locale(localeList.get(0), localeList.get(1)));
                    }
                }
            }
        }

        log.info("init i18n parameters success! application name:{}, nacos addr:{}, i18n messages namespace:{}", applicationName, serverAddr, i18nNamespace);
    }

    private void initTip(Locale locale) {
        String dataId = null;
        try {

            if (locale == null) {
                dataId = i18nMessagesProperties.getBasename();
            } else {
                dataId = i18nMessagesProperties.getBasename() + "_" + locale.getLanguage();
                if(StringUtil.isValid(locale.getCountry())){
                    dataId+=("_" + locale.getCountry());
                }
            }

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            if(StringUtil.isValid(i18nNamespace)){
                properties.put(PropertyKeyConst.NAMESPACE, i18nNamespace);
            }
            ConfigService configService = NacosFactory.createConfigService(properties);

            writeFileFromNacos(configService, dataId);
            setListener(configService, dataId);
        } catch (Exception e) {
            log.error(StringUtil.compose("i18n parameter [", dataId, "] have cached exception!"), e);
        }
    }

    private void setListener(ConfigService configService, String dataId) throws com.alibaba.nacos.api.exception.NacosException {
        configService.addListener(dataId, i18nGroup, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("receive new i18n parameters! refreshing...");
                try {
                    writeFileFromNacos(configService, dataId);
                } catch (Exception e) {
                    log.error("refresh i18n parameters exception!", e);
                }
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }

    private void writeFileFromNacos(ConfigService configService, String dataId){
        try {
            String content = configService.getConfig(dataId, i18nGroup, 5000);
            if (!StringUtil.isValid(content)) {
                log.warn("i18n parameter is null, skip this! dataId:{}", dataId);
                return;
            }
            log.info("i18n parameter[{}] will init!", dataId);
            saveAsFileWriter(dataId, content);
        } catch (Exception e) {
            log.error(StringUtil.compose("i18n parameter [", dataId, "] have cached exception!"), e);
        }
    }

    private void saveAsFileWriter(String dataId, String content) {
        try {
            String fileName = StringUtil.compose(System.getProperty("user.dir"), File.separator, i18nMessagesProperties.getBaseFolder(), File.separator, dataId, ".properties");
            File file = new File(fileName);
            FileUtils.writeStringToFile(file, content, "UTF-8", false);
            log.info("i18n parameters have cached! local cache file:{}", fileName);
        } catch (Exception e) {
            log.error("i18n parameter ["+dataId+"] have cached exception!", e);
        }
    }

    @Primary
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource messageSource(I18nMessagesProperties i18nMessagesProperties) {
        String cachePath = ResourceUtils.FILE_URL_PREFIX + System.getProperty("user.dir") + File.separator + i18nMessagesProperties.getBaseFolder() + File.separator + i18nMessagesProperties.getBasename();
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(cachePath);
        messageSource.setDefaultEncoding(i18nMessagesProperties.getEncoding());
        messageSource.setCacheMillis(i18nMessagesProperties.getCacheMillis());
        return messageSource;
    }

}
