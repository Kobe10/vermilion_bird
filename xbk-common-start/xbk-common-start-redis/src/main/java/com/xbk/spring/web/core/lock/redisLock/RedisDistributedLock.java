package com.xbk.spring.web.core.lock.redisLock;

import com.xbk.spring.web.core.lock.AbstractDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisDistributedLock extends AbstractDistributedLock {

    private final RedisTemplate redisTemplate;
    private final Set<String> lockKeySet = new HashSet<>();

    @Override
    public boolean lock(String key, long timeoutMillis, int retryTimes, long sleepMillis) {
        final String lockValue = UUID.randomUUID().toString();
        boolean redisSetLockResult = redisSetLock(key, lockValue, timeoutMillis);
        while ((!redisSetLockResult) && retryTimes-- > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                redisSetLockResult = redisSetLock(key, lockValue, timeoutMillis);
            } catch (InterruptedException e) {
                redisSetLockResult = false;
            }
        }
        if (redisSetLockResult) {
            lockKeySet.add(key);
            LockValueHandler.set(lockValue);
        }
        return redisSetLockResult;
    }

    @Override
    public boolean releaseLock(String key) {
        String lockValue = LockValueHandler.get();
        if (Objects.isNull(lockValue)) {
            return false;
        }
        final RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        final RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        final Object result = redisTemplate.execute(((RedisConnection connection) ->
                connection.eval(LuaScript.getUnLockScript().getBytes(),
                        ReturnType.INTEGER, 1,
                        keySerializer.serialize(key), valueSerializer.serialize(lockValue))
        ));
        boolean unLockResult = LuaScript.unLockResult(result);
        if (unLockResult) {
            lockKeySet.remove(key);
            LockValueHandler.clean();
        }
        return unLockResult;
    }

    /**
     * ??????
     *
     * @param key           key
     * @param value         ????????????????????? value
     * @param timeoutMillis ???????????????Millis
     * @return ??????????????????true
     */
    private boolean redisSetLock(String key, String value, long timeoutMillis) {
        final RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        final RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        Object execute = redisTemplate.execute((RedisConnection connection) ->
                connection.set(keySerializer.serialize(key), valueSerializer.serialize(value),
                        Expiration.milliseconds(timeoutMillis),
                        RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
        return Objects.nonNull(execute) && (Boolean) execute;
    }

    /**
     * ????????????????????????
     */
    public RedisDistributedLock(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //????????????????????? - ??????????????????????????????????????????
            if (this.lockKeySet.size() > 0) {
                log.info("?????????????????????redis?????????");
                redisTemplate.delete(lockKeySet);
            }
        }));
    }
}
