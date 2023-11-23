package com.soeun.GiftFunding.service;

import static com.soeun.GiftFunding.type.ErrorCode.PASSWORD_UNMATCHED;
import static com.soeun.GiftFunding.type.ErrorCode.USER_DUPLICATED;
import static com.soeun.GiftFunding.type.ErrorCode.USER_NOT_FOUND;

import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signin.Response;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.repository.UserRepository;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.type.ErrorCode;
import java.time.LocalDate;
import java.util.Optional;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
    @Override
    public Signup.Response singUp(Signup.Request request) {

        validateDuplicated(request);

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request.toEntity());

        log.info("{} 회원가입", request.getEmail());

        return Signup.Response.toResponse(request.getUserName());
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

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_UNMATCHED);
        }


        return Response.builder()
            .userName(user.getUsername())
            .role(user.getRole().name())
            .build();
    }

}
