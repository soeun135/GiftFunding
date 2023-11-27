package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorType.PASSWORD_UNMATCHED;
import static com.soeun.GiftFunding.type.ErrorType.USER_DUPLICATED;
import static com.soeun.GiftFunding.type.ErrorType.USER_NOT_FOUND;

import com.soeun.GiftFunding.dto.Reissue;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signin.Response;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.repository.UserRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

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
    public Reissue.Response reissue(Reissue.Request request) {
        return Reissue.Response.builder()
            .accessToken(tokenProvider.reIssueAccessToken(request.getRefreshToken()))
            .refreshToken(request.getRefreshToken())
            .build();
    }


}
