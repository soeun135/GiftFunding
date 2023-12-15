package com.soeun.GiftFunding.scheduler;


import com.soeun.GiftFunding.entity.Delivery;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.entity.Wallet;
import com.soeun.GiftFunding.mail.MailComponent;
import com.soeun.GiftFunding.mail.MailReceiverInterface;
import com.soeun.GiftFunding.mail.MailTemplate;
import com.soeun.GiftFunding.mail.MailTemplateRepository;
import com.soeun.GiftFunding.repository.DeliveryRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.type.DeliveryState;
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

    @Scheduled(cron = "0 0/1 * * * *")
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

    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void checkFundingSuccess() {
        Optional<MailTemplate> optionalMailTemplate =
            mailTemplateRepository.findByTemplateId("funding_expired");
        MailTemplate m = optionalMailTemplate.get();
        //마감일이든 아니든 펀딩 금액 달성
        //펀딩 금액 달성 그 직후 상태가 바껴야 함.
        fundingProductRepository.updateBySuccessFunding();

        //펀딩 금액 달성 FundingState -> Success로 바껴서 목록에도 숨겨지고
        // 다른 사용자의 후원 불가
        List<FundingProduct> successFunding =
            fundingProductRepository.findBySuccessFunding();


        for (FundingProduct e : successFunding) {
            mailComponent.send(e.getMember().getEmail(), e.getMember().getName(),
                m.getTitle(), m.getContents().replaceAll(
                        "\\{USER_NAME\\}", e.getMember().getName())
                    .replaceAll("\\{FUNDING_ID\\}", e.getId().toString()));

            deliveryRepository.save(
                Delivery.builder()
                    .fundingProduct(fundingProductRepository.findById(e.getId()).get())
                    .deliveryState(DeliveryState.WAIT)
                    .build()
            );
        }
    }
}
