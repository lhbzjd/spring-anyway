package ink.anyway.component.common.config;

import ink.anyway.component.common.s3.aws.AwsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "oss", name = "accessKey")
public class OssConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${oss.accessKey}")
    private String accessKey;

    @Value("${oss.secretKey}")
    private String secretKey;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.endpoint.is-domain}")
    private boolean isDomain;

    @Bean
    public AwsClient awsClient() {
        return AwsClient.builder().setAccessKey(this.accessKey).setSecretKey(this.secretKey).setEndpoint(this.endpoint).setDomain(isDomain).build();
    }

}
