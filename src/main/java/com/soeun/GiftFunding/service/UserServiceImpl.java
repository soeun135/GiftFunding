package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.repository.UserRepository;
import com.soeun.GiftFunding.type.ErrorCode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Signup.Response singUp(Signup.Request request) {
        //validation
        validateDuplicated(request);

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userRepository.save(request.toEntity());

        log.info("{} 회원가입", request.getEmail());

        return Signup.Response.toResponse(user.getUserName());
    }

    private void validateDuplicated(Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(ErrorCode.USER_DUPLICATED);
        }
    }
}
