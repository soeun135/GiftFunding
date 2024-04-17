package com.soeun.GiftFunding.scheduler;


import com.soeun.GiftFunding.domain.delivery.Delivery;
import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.mail.Mail;
import com.soeun.GiftFunding.mail.MailComponent;
import com.soeun.GiftFunding.mail.MailReceiverInterface;
import com.soeun.GiftFunding.mail.MailRepository;
import com.soeun.GiftFunding.mail.MailTemplate;
import com.soeun.GiftFunding.mail.MailTemplateRepository;
import com.soeun.GiftFunding.repository.DeliveryRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.domain.member.repository.MemberRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.type.DeliveryState;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundingScheduler {

    private final FundingProductRepository fundingProductRepository;
    private final MailComponent mailComponent;
    private final MailTemplateRepository mailTemplateRepository;
    private final WalletRepository walletRepository;
    private final DeliveryRepository deliveryRepository;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void fundingExpired() {
        log.info("실행");
        //마감일 3일 전인 항목에 대해 메일 발송
        Optional<MailTemplate> optionalMailTemplate =
            mailTemplateRepository.findByTemplateId("expiredate_remain_3_day");
        MailTemplate m = optionalMailTemplate.get();

        List<MailReceiverInterface> receiverList =
            fundingProductRepository.findByExpiredThreeDay();

        for (MailReceiverInterface e : receiverList) {
            log.info(e.getEmail());
            mailComponent.send(e.getEmail(), e.getName(),
                m.getTitle(), m.getContents().replaceAll(
                        "\\{USER_NAME\\}", e.getName())
                    .replaceAll("\\{FUNDING_ID\\}", e.getId().toString()));
        }

        //마감일 도래한 펀딩
        fundingProductRepository.updateByExpiredWithFail();

        List<FundingProduct> failFunding =
            fundingProductRepository.findByFailFunding();

        failFunding.forEach(f -> {
                Wallet wallet = walletRepository.findByMember(f.getMember());
                wallet.setBalance(wallet.getBalance() + f.getTotal());
            }
        );
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    @Transactional
    public void checkFundingSuccess() {
        Optional<MailTemplate> optionalMailTemplate =
            mailTemplateRepository.findByTemplateId("funding_expired");
        MailTemplate m = optionalMailTemplate.get();

        Mail mail = mailRepository.findByMailTitle("펀딩 성공 메일")
            .orElse(new Mail(1,
                "펀딩 성공 메일",
                LocalDateTime.now(),
                LocalDateTime.now()));

          Timestamp lastestSendAt = Timestamp.valueOf(mail.getUpdatedAt());

        List<FundingProduct> successFunding =
            fundingProductRepository.findBySuccessFunding(lastestSendAt);
        // 펀딩이 완료돼 업데이트 된 시간과 메일을 마지막으로 보낸 시간을 비교핵서
        //3600(60)초 이하이면 메일 보내야하는 대상들.

        if (successFunding.size() > 0) {
            for (FundingProduct e : successFunding) {
                Member member = memberRepository.getReferenceById(e.getMember().getId());
                mailComponent.send(member.getEmail(), member.getName(),
                    m.getTitle(), m.getContents().replaceAll(
                            "\\{USER_NAME\\}", member.getName())
                        .replaceAll("\\{FUNDING_ID\\}", e.getId().toString()));

                deliveryRepository.save(
                    Delivery.builder()
                        .fundingProduct(fundingProductRepository.findById(e.getId()).get())
                        .deliveryState(DeliveryState.WAIT)
                        .build()
                );
            }
        }

        if (mailRepository.countByMailTitle("펀딩 성공 메일") < 1) {
            mailRepository.save(Mail.builder()
                .mailTitle("펀딩 성공 메일")
                .build());
        }
        mailRepository.findByMailTitleAndUpdate();
    }


}
