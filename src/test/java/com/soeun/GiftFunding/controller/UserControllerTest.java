package com.soeun.GiftFunding.controller;

import static com.soeun.GiftFunding.type.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.Signup.Request;
import com.soeun.GiftFunding.dto.Signup.Response;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 성공")
    void successSignup() throws Exception{
        //given
        Request request = Request.builder()
            .name("soni")
            .phone("01011111111")
            .email("test@naver.com")
            .password("1111") //인코딩해서 넣어야함
            .address("경기도")
            .birthDay("20000128")
            .build();

        given(userService.singUp(any()))
            .willReturn(Response.builder()
                .name(request.getName())
                .message("님 회원가입이 완료되었습니다.")
                .build());
        //when
        //then
        mockMvc.perform(post("/user/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value(request.getName()))
            //.andExpect(content().string(request.getUserName()+"님 회원가입이 완료되었습니다."))
            .andDo(print());
     }

     @Test
     @DisplayName("회원 가입 실패 - 중복회원 존재")
     void failSignup() throws Exception {
         //given
         Request request = Request.builder()
             .name("soni")
             .phone("01011111111")
             .email("test@naver.com")
             .password("1111") //인코딩해서 넣어야함
             .address("경기도")
             .birthDay("20000128")
             .build();

         given(userService.singUp(any()))
             .willThrow(new UserException(USER_DUPLICATED));
         //when
         //then
         mockMvc.perform(post("/user/signup")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print()) //응답값을 화면에 표시해줌
             .andExpect(jsonPath("$.errorCode").value("USER_DUPLICATED"))
             .andExpect(jsonPath("$.errorMessage").value("중복된 이메일입니다."))
             .andExpect(status().isOk());
      }
}