package com.soeun.GiftFunding.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedissonLock {
    long waitTime() default 5000; // Lock 획득을 시도하는 최대시간(ms)
    long leaseTime() default 5000L; //Lock 획득 후 점유하는 최대시간(ms)
}
