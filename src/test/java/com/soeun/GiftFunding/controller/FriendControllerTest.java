package com.soeun.GiftFunding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.UserAdapter;
import com.soeun.GiftFunding.exception.CustomAuthenticationEntryPoint;
import com.soeun.GiftFunding.mock.WithMockUser;
import com.soeun.GiftFunding.security.GetAuthentication;
import com.soeun.GiftFunding.security.TokenProvider;
import com.soeun.GiftFunding.service.FriendServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
class FriendControllerTest {
    @MockBean
    private FriendServiceImpl friendService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private GetAuthentication getAuthentication;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    @DisplayName("친구요청 성공 테스트")
    void successRequestTest() throws Exception {
        //given
        UserAdapter userAdapter =
                new UserAdapter("soni@naver.com", "abcd");
//                UserAdapter.from(
//                        MemberFixture.soni.createMember());

        FriendRequest.Request request
                = FriendRequest.Request.builder()
                .email("buny@naver.com")
                .build();

        given(friendService.request(request, userAdapter))
                .willReturn(FriendRequest.Response.builder()
                        .email(request.getEmail())
                        .message("님에게 친구요청을 보냈습니다.")
                        .build());
        //when
        //then
        mockMvc.perform(post("/friend/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
//                        .with(user(userAdapter)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void teest() throws Exception {
        //given
        UserAdapter userAdapter =
//                UserAdapter.from(MemberFixture.soni.createMember());
//
                new UserAdapter("sonfgi@naver.com", "abcd");
        //when
        //then
        mockMvc.perform(get("/friend/hi")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                        .with(user(userAdapter))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}