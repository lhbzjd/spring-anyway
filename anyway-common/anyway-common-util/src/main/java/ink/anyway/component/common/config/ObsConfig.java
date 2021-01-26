package ink.anyway.component.common.config;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "obs", name = "accessKey")
public class ObsConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${obs.accessKey}")
    private String accessKey;

    @Value("${obs.secretKey}")
    private String secretKey;

    @Value("${obs.endpoint}")
    private String endpoint;

    @Bean
    public ObsClient obsClient() {
        ObsClient obsClient = null;
        try {
            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setEndPoint(endpoint);
            obsClient = new ObsClient(accessKey, secretKey, config);
        }catch (Exception e){
            logger.warn("构建华为OBS客户端失败!", e);
        }
        return obsClient;
    }

}
