package ink.anyway.component.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@RefreshScope
@Service
@ConfigurationProperties(prefix = "spring.anyway.messages")
public class I18nMessagesProperties {

    /**
     * 国际化文件缓存目录
     */
    private String baseFolder;

    /**
     * 国际化文件基础名称
     */
    private String basename;

    /**
     * 国际化编码格式
     */
    private String encoding;

    /**
     * 缓存刷新的时间间隔
     */
    private long cacheMillis;

    private String nacosNamespace;

    private String nacosGroup;

    /**
     * 提供支持的语言集合
     */
    private List<String> localeCollection;

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    public String getBasename() {
        return basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public long getCacheMillis() {
        return cacheMillis;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    public String getNacosNamespace() {
        return nacosNamespace;
    }

    public void setNacosNamespace(String nacosNamespace) {
        this.nacosNamespace = nacosNamespace;
    }

    public String getNacosGroup() {
        return nacosGroup;
    }

    public void setNacosGroup(String nacosGroup) {
        this.nacosGroup = nacosGroup;
    }

    public List<String> getLocaleCollection() {
        return localeCollection;
    }

    public void setLocaleCollection(List<String> localeCollection) {
        this.localeCollection = localeCollection;
    }
}
