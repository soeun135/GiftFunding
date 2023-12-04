package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.PASSWORD_UNMATCHED;
import static com.soeun.GiftFunding.type.ErrorType.USER_DUPLICATED;
import static com.soeun.GiftFunding.type.ErrorType.USER_NOT_FOUND;

import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signin.Response;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.entity.Member;
import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.MemberRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final FundingProductRepository fundingProductRepository;

    @Override
    public UserDetails loadUserByUsername(String mail)
        throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(mail)
            .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        return new UserAdapter(
            member, member.getEmail(), member.getPassword());

    }

    @Override
    public String signUp(Signup.Request request) {

        validateDuplicated(request);

        request.setPassword(passwordEncoder.encode(
            request.getPassword()));
        memberRepository.save(request.toEntity());

        log.info("{} 회원가입", request.getEmail());
        log.info(request.getName());
        return Signup.Response.toResponse(request.getName());
    }

    private void validateDuplicated(Request request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(USER_DUPLICATED);
        }
    }

    @Override
    public Response signIn(Signin.Request request) {
        Member user = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        validatedPassword(request, user);

        String mail = user.getEmail();

        return Response.builder()
            .accessToken(tokenProvider.generateAccessToken(mail))
            .refreshToken(tokenProvider.generateRefreshToken(mail))
            .build();
    }

    private void validatedPassword(Signin.Request request, Member user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new MemberException(PASSWORD_UNMATCHED);
        }
    }

    @Override
    public ReissueResponse reissue(String request) {
        String refreshToken =
            this.resolveTokenFromRequest(request);


        return ReissueResponse.builder()
            .accessToken(
                tokenProvider.reIssueAccessToken(refreshToken))
            .refreshToken(refreshToken)
            .build();
    }
    private String resolveTokenFromRequest(String token) {

        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring("Bearer ".length());
        }

        return token;
    }
    @Override
    public UserInfoResponse userInfo(UserAdapter userAdapter) {
        Member member = userAdapter.getMember();
        //user 정보 + 해당 user의 펀딩 상품을 조회해서 dto객체로 리턴
        return UserInfoResponse.builder()
            .name(member.getName())
            .phone(member.getPhone())
            .email(member.getEmail())
            .address(member.getAddress())
            .birthDay(member.getBirthDay())
            .fundingProductList(fundingProductRepository.findByMemberId(member.getId()))
            .build();
    }

    @Override
    @Transactional
    public UpdateInfo.Response update(UpdateInfo.Request request,
        UserAdapter userAdapter) {
        Member member = userAdapter.getMember();

        updateCheck(request, member);

        return member.toDto();
    }

    private void updateCheck(UpdateInfo.Request request, Member member) {
        if (StringUtils.hasText(request.getName())) {
            member.setName(request.getName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            member.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getAddress())) {
            member.setAddress(request.getAddress());
        }
        if (!ObjectUtils.isEmpty(request.getBirthDay())) {
            member.setBirthDay(request.getBirthDay());
        }
    }


}
