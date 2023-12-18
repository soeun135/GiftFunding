package com.soeun.GiftFunding.mail;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    Optional<Mail> findByMailTitle(String mailTitle);
    @Modifying
    @Query(value = "update mail set updated_at = now()"
        + " where mail_title = '펀딩 성공 메일';"
        , nativeQuery = true)
    void findByMailTitleAndUpdate();

    long countByMailTitle(String mailTitle);
}

