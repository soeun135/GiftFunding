package com.soeun.GiftFunding.scheduler;


import com.soeun.GiftFunding.entity.Delivery;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.mail.Mail;
import com.soeun.GiftFunding.mail.MailComponent;
import com.soeun.GiftFunding.mail.MailReceiverInterface;
import com.soeun.GiftFunding.mail.MailRepository;
import com.soeun.GiftFunding.mail.MailTemplate;
import com.soeun.GiftFunding.mail.MailTemplateRepository;
import com.soeun.GiftFunding.repository.DeliveryRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
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

        //마감일 도래한 상품의 펀딩 목록
        fundingProductRepository.updateByExpiredWithFail();
        // 1. 펀딩 상품 목록에서 숨겨짐 -> update Funding_state fail로 처리

        // 2. 펀딩자의 지갑으로 펀딩 금액만큼 반환
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
        // 펀딩 금액 달성한 항목들 중에서 메일 가장 최근에 보낸 시간보다
        //나중에 성공상태 된 것
        Mail mail = mailRepository.findByMailTitle("펀딩 성공 메일")
            .orElse(new Mail(1,
                "펀딩 성공 메일",
                LocalDateTime.now(),
                LocalDateTime.now()));

        //메일 레포지토리에 아무것도 없을 떄 지금으로부터 한 시간 전의 (1분)
        //시간을 lastestSendAt으로 지정. 있으면 그 항목의 updated 시간을 가져옴
        Timestamp lastestSendAt = Timestamp.valueOf(mail.getUpdatedAt());
        log.info(String.valueOf(lastestSendAt));
        List<FundingProduct> successFunding =
            fundingProductRepository.findBySuccessFunding(lastestSendAt);
        // 펀딩이 완료돼 업데이트 된 시간과 메일을 마지막으로 보낸 시간을 비교핵서
        //3600(60)초 이하이면 메일 보내야하는 대상들.

        //유효한 시간에 해당하는 항목들에 메일을 보냄.
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
