package ink.anyway.component.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConfigurationProperties(prefix = "spring.cache.caffeine")
public class CaffeineProperties {

    private Map<String, Map<String, Integer>> caches;

    public Map<String, Map<String, Integer>> getCaches() {
        return caches;
    }

    public void setCaches(Map<String, Map<String, Integer>> caches) {
        this.caches = caches;
    }
}
