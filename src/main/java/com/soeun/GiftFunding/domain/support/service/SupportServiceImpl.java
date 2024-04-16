package com.soeun.GiftFunding.domain.support.service;

import static com.soeun.GiftFunding.type.ErrorType.BALANCE_NOT_ENOUGH;
import static com.soeun.GiftFunding.type.ErrorType.CANCEL_TIME_OUT;
import static com.soeun.GiftFunding.type.ErrorType.FRIEND_INFO_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.FUNDING_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.SUPPORT_EXCEED_WHOLE_PRICE;
import static com.soeun.GiftFunding.type.ErrorType.SUPPORT_NOT_FOUND;
import static com.soeun.GiftFunding.type.ErrorType.USER_NOT_FOUND;
import static com.soeun.GiftFunding.type.FriendState.ACCEPT;
import static com.soeun.GiftFunding.type.SupportState.CANCEL;
import static com.soeun.GiftFunding.type.SupportState.COMPLETE;

import com.soeun.GiftFunding.domain.support.entity.SupportDetail;
import com.soeun.GiftFunding.dto.SupportCancelResponse;
import com.soeun.GiftFunding.dto.SupportInfo.Request;
import com.soeun.GiftFunding.dto.SupportInfo.Response;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.domain.fundingProduct.entity.FundingProduct;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.domain.transaction.Transaction;
import com.soeun.GiftFunding.domain.wallet.Wallet;
import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.exception.SupportException;
import com.soeun.GiftFunding.domain.friend.repository.FriendRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.domain.member.repository.MemberRepository;
import com.soeun.GiftFunding.domain.support.SupportDetailRepository;
import com.soeun.GiftFunding.repository.TransactionRepository;
import com.soeun.GiftFunding.repository.WalletRepository;
import com.soeun.GiftFunding.type.FundingState;
import com.soeun.GiftFunding.type.TransactionType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final SupportDetailRepository supportDetailRepository;
    private final MemberRepository memberRepository;
    private final FundingProductRepository fundingProductRepository;
    private final FriendRepository friendRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public Response support(
            MemberAdapter memberAdapter, long id, Request request) {
        Member member = memberRepository.findByEmail(memberAdapter.getUsername())
            .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        Member friend = memberRepository.findById(request.getFriendId())
            .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        Wallet wallet = walletRepository.findByMember(member);

        FundingProduct fundingProduct =
            fundingProductRepository.findByIdAndFundingStateAndMember(
                id, FundingState.ONGOING, friend);

        validateSupport(request, member, friend, wallet, fundingProduct);

        supportDetailRepository.save(
            SupportDetail.builder()
                .fundingProduct(fundingProduct)
                .wallet(wallet)
                .supportAmount(request.getSupportPrice())
                .supportState(COMPLETE)
                .build()
        );
        fundingProduct.setTotal(
            fundingProduct.getTotal() + request.getSupportPrice());

        wallet.setBalance(
            wallet.getBalance() - request.getSupportPrice()
        );
        transactionRepository.save(Transaction.builder()
            .wallet(wallet)
            .transactionType(TransactionType.USE)
            .transactionAmount(request.getSupportPrice()).build());

        return Response.builder()
            .fundingProductDto(fundingProduct.toDto())
            .build();
    }

    private void validateSupport(Request request, Member member, Member friend,
        Wallet wallet, FundingProduct fundingProduct) {

        if (ObjectUtils.isEmpty(fundingProduct)) {
            throw new SupportException(FUNDING_NOT_FOUND);
        }
        if (wallet.getBalance() < request.getSupportPrice()) {
            throw new SupportException(BALANCE_NOT_ENOUGH);
        }
        if (!friendRepository.existsByFriendStateAndMemberRequestAndMember(
            ACCEPT, member, friend)) {
            throw new SupportException(FRIEND_INFO_NOT_FOUND);
        }

        if (fundingProduct.getProduct().getPrice() <
            fundingProduct.getTotal() + request.getSupportPrice()) {
            throw new SupportException(SUPPORT_EXCEED_WHOLE_PRICE);
        }

        if (fundingProduct.getProduct().getPrice() ==
            fundingProduct.getTotal() + request.getSupportPrice()) {
            fundingProduct.setFundingState(FundingState.SUCCESS);
        }
    }

    @Transactional
    @Override
    public SupportCancelResponse supportCancel(MemberAdapter memberAdapter, long id) {
        Member member = memberRepository.findByEmail(memberAdapter.getUsername())
            .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        Wallet wallet = walletRepository.findByMember(member);

        SupportDetail supportDetail =
            supportDetailRepository.findByIdAndWalletAndSupportState(
                id, wallet, COMPLETE);

        validateSupportCancel(supportDetail);

        wallet.setBalance(
            wallet.getBalance() + supportDetail.getSupportAmount());

        supportDetail.setSupportState(CANCEL);

        FundingProduct fundingProduct = supportDetail.getFundingProduct();
        fundingProduct.setTotal(
            fundingProduct.getTotal() - supportDetail.getSupportAmount()
        );
        transactionRepository.save(
            Transaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.CANCEL)
                .transactionAmount(supportDetail.getSupportAmount())
                .build());

        return SupportCancelResponse.builder()
            .supportDetailId(id)
            .message("번 후원 내역의 취소가 완료되었습니다.")
            .build();
    }

    private void validateSupportCancel(SupportDetail supportDetail) {
        if (ObjectUtils.isEmpty(supportDetail)) {
            throw new SupportException(SUPPORT_NOT_FOUND);
        }

        if (!LocalDateTime.now().isBefore(
            supportDetail.getCreatedAt().plusHours(1))) {
            throw new SupportException(CANCEL_TIME_OUT);
        }
    }
}
