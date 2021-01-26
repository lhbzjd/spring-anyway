package ink.anyway.component.common.redis;

import ink.anyway.component.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@ConditionalOnProperty(prefix = "db.redis", name = "password")
public class JedisFacade implements DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisSentinelPool jedisPool;

    private boolean autoDestroyPool = true;

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

    @PostConstruct
    public void initPool() throws BeanCreationException {
        if (!StringUtil.isValid(redisSentinels)){
            throw new BeanCreationException("${db.redis.sentinels} is null");
        }

        if (!StringUtil.isValid(password)){
            throw new BeanCreationException("${db.redis.password} is null");
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);/** 最大连接数50*/
        poolConfig.setMinIdle(minIdle);/** 最小空闲连接数10*/
        poolConfig.setMaxIdle(maxIdle);/** 最大空闲连接数30*/
        poolConfig.setBlockWhenExhausted(true);/** 连接耗尽时是否阻塞，false报异常，true阻塞知道超时*/
        Set<String> sentinels = new HashSet<>();
        String[] sentinelArr = redisSentinels.trim().split(",");
        for(String sentinel:sentinelArr){
            if(StringUtil.isValid(sentinel)){
                sentinels.add(sentinel.trim());
            }
        }
        if(sentinels.size()>0){
            jedisPool = new JedisSentinelPool("cloudMaster", sentinels, poolConfig, timeout, password);
        }else{
            throw new BeanCreationException("${db.redis.sentinels} is null");
        }
    }

    private Jedis getJedis(){
        if(jedisPool.isClosed()){
            logger.warn("after JedisSentinelPool have destroyed, application need Jedis, so create a Jedis out of pool.");
            Jedis jedis = new Jedis(this.jedisPool.getCurrentHostMaster().getHost(), this.jedisPool.getCurrentHostMaster().getPort());
            jedis.auth(password);
            return jedis;
        }else {
            return jedisPool.getResource();
        }
    }

    public String setWithExpire(String key, String value, int seconds){
        Jedis jedis = this.getJedis();
        String res = jedis.set(key, value);
        jedis.expire(key, seconds);
        jedis.close();
        return res;
    }

    public String get(String key){
        Jedis jedis = this.getJedis();
        String res = jedis.get(key);
        jedis.close();
        return res;
    }

    public Long hset(String key, String field, String value){
        Jedis jedis = this.getJedis();
        Long res = jedis.hset(key, field, value);
        jedis.close();
        return res;
    }

    public String hget(String key, String field){
        Jedis jedis = this.getJedis();
        String res = jedis.hget(key, field);
        jedis.close();
        return res;
    }

    public Long hdel(String key, String... fields){
        Jedis jedis = this.getJedis();
        Long res = jedis.hdel(key, fields);
        jedis.close();
        return res;
    }

    public Map<String, String> hgetAll(String key){
        Jedis jedis = this.getJedis();
        Map<String, String> res = jedis.hgetAll(key);
        jedis.close();
        return res;
    }

    public Long del(String key){
        Jedis jedis = this.getJedis();
        Long res = jedis.del(key);
        jedis.close();
        return res;
    }

    @Override
    public void destroy() throws Exception {
        if(autoDestroyPool)
            this.jedisPool.destroy();
    }
}
