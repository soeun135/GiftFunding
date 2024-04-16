package com.soeun.GiftFunding.scheduler;

import com.soeun.GiftFunding.domain.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendScheduler {
    private final FriendRepository friendRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void removeRejectRequest() {
        log.info("reject 항목 제거 스켸줄러 실행");
        friendRepository.deleteByRejectFriend();
    }
}
