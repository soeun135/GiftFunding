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
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.repository.UserRepository;
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
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final FundingProductRepository fundingProductRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        return userRepository.findByEmail(mail)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    @Override
    public Signup.Response singUp(Signup.Request request) {

        validateDuplicated(request);

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request.toEntity());

        log.info("{} 회원가입", request.getEmail());

        return Signup.Response.toResponse(request.getName());
    }

    private void validateDuplicated(Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(USER_DUPLICATED);
        }
    }

    @Override
    public Response signIn(Signin.Request request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        validatedPassword(request, user);

        String mail = user.getEmail();

        return Response.builder()
            .accessToken(tokenProvider.generateAccessToken(mail))
            .refreshToken(tokenProvider.generateRefreshToken(mail))
            .build();
    }

    private void validatedPassword(Signin.Request request, User user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_UNMATCHED);
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
    public UserInfoResponse userInfo(String token) {
        //헤더에서 토큰 꺼내오기 + 토큰에서 메일 꺼내오기
        String mail = tokenProvider.getMail(
            this.resolveTokenFromRequest(token));

        //꺼내온 메일로 USER 조회
        User user = userRepository.findByEmail(mail)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        //user 정보 + 해당 user의 펀딩 상품을 조회해서 dto객체로 리턴
        return UserInfoResponse.builder()
            .name(user.getName())
            .phone(user.getPhone())
            .email(user.getEmail())
            .address(user.getAddress())
            .birthDay(user.getBirthDay())
            .fundingProductList(fundingProductRepository.findByUserId(user.getId()))
            .build();
    }

    @Override
    @Transactional
    public UpdateInfo.Response update(UpdateInfo.Request request, String token) {
        String mail = tokenProvider.getMail(
            this.resolveTokenFromRequest(token));
        User user = userRepository.findByEmail(mail)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        updateCheck(request, user);

        return user.toDto();
    }

    private void updateCheck(UpdateInfo.Request request, User user) {
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (!ObjectUtils.isEmpty(request.getBirthDay())) {
            user.setBirthDay(request.getBirthDay());
        }
    }


}
