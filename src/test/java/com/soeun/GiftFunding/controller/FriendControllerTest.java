package com.soeun.GiftFunding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.config.SecurityConfig;
import com.soeun.GiftFunding.dto.FriendRequest;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.mock.WithMockUser;
import com.soeun.GiftFunding.security.JwtAuthenticationFilter;
import com.soeun.GiftFunding.service.FriendServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
}