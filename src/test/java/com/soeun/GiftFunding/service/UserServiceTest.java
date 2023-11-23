package com.soeun.GiftFunding.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.dto.Signup.Response;
import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceimpl;

    @Test
    void successSignup() {
        //given
        Request request = Request.builder()
            .name("soni")
            .phone("01011111111")
            .email("test@naver.com")
            .password("1111") //인코딩해서 넣어야함
            .address("경기도")
            .birthDay("2000-01-28")
            .build();

        given(userRepository.save(any()))
            .willReturn(User
                .builder()
                .name("soni")
                .phone("01011111111")
                .email("test@naver.com")
                .password("1111") //인코딩해서 넣어야함
                .address("경기도")
                .birthDay(LocalDate.parse("2000-01-28"))
                .build());
        //when
        ArgumentCaptor<User> captor =
            ArgumentCaptor.forClass(User.class);

        Response response = userServiceimpl.singUp(request);

        //then
        verify(userRepository, times(1))
            .save(captor.capture());
        assertEquals(request.getName(),response.getName());
        assertEquals(request.getName(),captor.getValue().getName());

     }
}