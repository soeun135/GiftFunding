package com.soeun.GiftFunding.aop;

import com.soeun.GiftFunding.exception.FriendException;
import com.soeun.GiftFunding.type.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String email) {
        RLock lock = redissonClient.getLock(email);
        log.debug("Trying lock for email : {}", email);

        try {
            boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);

            if (!isLock) {
                log.error("==== Lock acquisition failed ====");
                throw new FriendException(ErrorType.FRIEND_REQUEST_LOCK);
            }
        } catch (FriendException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed", e);
        }
    }

    public void unlock(String email) {
        log.debug("Unlock for email : {}", email);
        redissonClient.getLock(email).unlock();
    }
}
