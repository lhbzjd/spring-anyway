package ink.anyway.component.common.lock;

import ink.anyway.component.common.redis.RedissonFacade;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnBean(value=RedissonFacade.class)
public class DistributedLock {

    private final String disLockPrefix = "Any-DIST-LOCK-";

    @Autowired
    private RedissonFacade redissonFacade;

    public RLock getLock(String key){
        return redissonFacade.getRedissonClient().getLock(disLockPrefix+key);
    }

    public RLock getFairLock(String key){
        return redissonFacade.getRedissonClient().getFairLock(disLockPrefix+key);
    }

    public RLock getSpinLock(String key){
        return redissonFacade.getRedissonClient().getSpinLock(disLockPrefix+key);
    }

    public RReadWriteLock getReadWriteLock(String key){
        return redissonFacade.getRedissonClient().getReadWriteLock(disLockPrefix+key);
    }

    public RLock getMultiLock(RLock... locks){
        return redissonFacade.getRedissonClient().getMultiLock(locks);
    }

    public boolean lock(String key, long leaseMilliseconds){
        this.getLock(key).lock(leaseMilliseconds, TimeUnit.MILLISECONDS);
        return  true;
    }

    public boolean unlock(String key){
        this.getLock(key).unlock();
        return  true;
    }

}
