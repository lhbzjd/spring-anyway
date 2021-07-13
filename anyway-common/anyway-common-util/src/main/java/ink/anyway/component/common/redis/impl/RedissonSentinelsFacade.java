package ink.anyway.component.common.redis.impl;

import ink.anyway.component.common.redis.RedissonFacade;
import ink.anyway.component.common.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "db.redis", name = "sentinels")
public class RedissonSentinelsFacade implements RedissonFacade {

    private RedissonClient redissonClient;

    @Value("${db.redis.sentinels}")
    private String redisSentinels;

    @Value("${db.redis.password}")
    private String password;

    @Value("${db.redis.timeout}")
    private int timeout = 2000;

    @Value("${db.redis.maxTotal}")
    private int maxTotal;

    @Value("${db.redis.maxIdle}")
    private int maxIdle;

    @Value("${db.redis.minIdle}")
    private int minIdle;

    @Override
    public RedissonClient getRedissonClient() {
        return this.redissonClient;
    }

    @PostConstruct
    public void  init(){
        Config config = new Config();

        List<String> sentinels = new ArrayList<>();
        String[] sentinelArr = redisSentinels.trim().split(",");
        for(String sentinel:sentinelArr){
            if(StringUtil.isValid(sentinel)&&!sentinels.contains("redis://"+sentinel.trim())){
                sentinels.add("redis://"+sentinel.trim());
            }
        }

        if(sentinels.size()>0){
            config.useSentinelServers().setMasterName("cloudMaster");
            if(sentinels.size()<2)
                config.useSentinelServers().setCheckSentinelsList(false);
            else
                config.useSentinelServers().setCheckSentinelsList(true);
            config.useSentinelServers().setSentinelAddresses(sentinels);
            config.useSentinelServers().setSentinelPassword(password);
            config.useSentinelServers().setPassword(password);
            config.useSentinelServers().setConnectTimeout(timeout);
            config.useSentinelServers().setMasterConnectionPoolSize(maxTotal);
            config.useSentinelServers().setMasterConnectionMinimumIdleSize(minIdle);
            config.useSentinelServers().setSlaveConnectionPoolSize(maxTotal);
            config.useSentinelServers().setSlaveConnectionMinimumIdleSize(minIdle);
        }

        redissonClient = Redisson.create(config);
    }
}
