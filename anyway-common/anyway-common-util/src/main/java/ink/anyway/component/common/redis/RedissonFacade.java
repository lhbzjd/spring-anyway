package ink.anyway.component.common.redis;

import org.redisson.api.RedissonClient;

public interface RedissonFacade {

    public RedissonClient getRedissonClient();

}
