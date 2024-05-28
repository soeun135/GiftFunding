package com.soeun.GiftFunding.aop;

import com.soeun.GiftFunding.dto.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LockAopAspect {
    private final RedissonClient redissonClient;
    private final LockService lockService;

    @Around("@annotation(com.soeun.GiftFunding.aop.RedissonLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            FriendRequest.Request request
    ) throws Throwable {
        lockService.lock(request.getEmail());

        try {
            return pjp.proceed();

        } finally {
            lockService.unlock(request.getEmail());
        }
    }
}
