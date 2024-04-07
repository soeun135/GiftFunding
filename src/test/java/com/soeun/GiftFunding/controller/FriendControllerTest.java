package com.soeun.GiftFunding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.config.SecurityConfig;
import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.mock.WithMockUser;
import com.soeun.GiftFunding.security.JwtAuthenticationFilter;
import com.soeun.GiftFunding.service.FriendServiceImpl;
import com.soeun.GiftFunding.type.FriendState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.soeun.GiftFunding.type.FriendState.ACCEPT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FriendController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
class FriendControllerTest {
    @MockBean
    private FriendServiceImpl friendService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    @DisplayName("친구요청 성공 테스트")
    void successRequestTest() throws Exception {
        //given
        MemberAdapter memberAdapter =
                new MemberAdapter("sodfni@naver.com", "afdfbcd");
//                MemberAdapter.from(MemberFixture.soni.createMember());

        FriendRequest.Request request
                = FriendRequest.Request.builder()
                .email("buny@naver.com")
                .build();

        when(friendService.request(any(), any()))
                .thenReturn(FriendRequest.Response.builder()
                        .email(request.getEmail())
                        .message("님에게 친구요청을 보냈습니다.")
                        .build());
        //when
        //then
        mockMvc.perform(post("/friend/request")
                        .header("Authorization", "Bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.message").value("님에게 친구요청을 보냈습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("친구 목록 리스트 조회 테스트")
    void friendListTest() throws Exception {
        //given
        FriendState friendState = ACCEPT;
        Pageable pageable = PageRequest.of(0, 10);

        List friendList = Arrays.asList(
                FriendList.builder()
                        .memberName("버니")
                        .memberEmail("buni@naver.com")
                        .build(),
                FriendList.builder()
                        .memberName("벅스")
                        .memberEmail("bucks@naver.com")
                        .build()
        );

        Page<FriendList> pageResult =
                new PageImpl<>(friendList);

        given(friendService.friendList(any(), any(), any()))
                .willReturn(pageResult);

        //when
        //then
        mockMvc.perform(get("/friend")
                        .header("Authorization", "Bearer AccessToken")
                        .param("page", "0")
                        .param("size", "10")
                        .param("friendState", "ACCEPT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].memberName").value("버니"))
                .andExpect(jsonPath("$.content.[0].memberEmail").value("buni@naver.com"))
                .andExpect(jsonPath("$.content.[1].memberName").value("벅스"))
                .andExpect(jsonPath("$.content.[1].memberEmail").value("bucks@naver.com"));
    }
    @Test
    @DisplayName("친구 요청 동시성 테스트")
    void redissonLockTest() {
        //given

        //when
        //then
     }
}