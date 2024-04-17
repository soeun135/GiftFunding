package com.soeun.GiftFunding.domain.member.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.config.SecurityConfig;
import com.soeun.GiftFunding.domain.member.dto.Signin;
import com.soeun.GiftFunding.domain.member.dto.Signup;
import com.soeun.GiftFunding.domain.member.service.MemberServiceImpl;
import com.soeun.GiftFunding.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
@AutoConfigureRestDocs
class MemberControllerTest {
    @MockBean
    private MemberServiceImpl memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void singUpSuccessTest() throws Exception {
        //given
        Signup.Request request =
                Signup.Request.builder()
                        .name("소은")
                        .phone("010-1111-1111")
                        .email("soni@naver.com")
                        .password("qwerty")
                        .address("서울특별시 강남구")
                        .birthDay("2000-01-28")
                        .build();

        given(memberService.signUp(any()))
                .willReturn(new Signup.Response(
                        "소은", "님 회원가입이 완료되었습니다."));
        //when
        //then
        mockMvc.perform(post("/member/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("소은"))
                .andDo(MockMvcRestDocumentationWrapper.document("/member/signup",
                        ResourceSnippetParameters.builder()
                                .tag("member")
                                .summary("signup API")
                                .description("회원가입")
                                .requestSchema(Schema.schema("Signup.Request"))
                                .responseSchema(Schema.schema("Signup.Response"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                , fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                                , fieldWithPath("birthDay").type(JsonFieldType.STRING).description("생일")
                        )
                        , responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("message").type(JsonFieldType.STRING).description("성공메세지")
                        )));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void singInSuccessTest() throws Exception {
        //given
        Signin.Request request =
                new Signin.Request("soni@naver.com", "qwerty");

        given(memberService.signIn(any()))
                .willReturn(new Signin.Response(
                        "AccessToken", "RefreshToken"));

        //when
        //then
        mockMvc.perform(post("/member/signin")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("AccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("RefreshToken"))
                .andDo(MockMvcRestDocumentationWrapper.document("/member/signin",
                                ResourceSnippetParameters.builder()
                                        .tag("member")
                                        .summary("signin API")
                                        .description("로그인")
                                        .requestSchema(Schema.schema("Signin.Request"))
                                        .responseSchema(Schema.schema("Signin.Response"))
                                , requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                        , fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                )
                                , responseFields(
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("AccessToken")
                                        , fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("RefreshToken")
                                )
                        )
                );
    }
}