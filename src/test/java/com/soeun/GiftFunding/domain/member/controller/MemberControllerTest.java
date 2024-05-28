package com.soeun.GiftFunding.domain.member.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.config.SecurityConfig;
import com.soeun.GiftFunding.controller.MemberController;
import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.dto.UserInfoResponse;
import com.soeun.GiftFunding.service.MemberServiceImpl;
import com.soeun.GiftFunding.entity.Product;
import com.soeun.GiftFunding.dto.FundingProductDto;
import com.soeun.GiftFunding.dto.MemberAdapter;
import com.soeun.GiftFunding.dto.ReissueResponse;
import com.soeun.GiftFunding.mock.WithMockUser;
import com.soeun.GiftFunding.security.JwtAuthenticationFilter;
import com.soeun.GiftFunding.type.FundingState;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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

    private MemberAdapter memberAdapter1() {
        return new MemberAdapter("soni@naver.com", "qwerty");
    }

    @WithMockUser
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
                .andDo(document("member/signup",
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

    @WithMockUser
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
                .andDo(document("member/signin",
                                ResourceSnippetParameters.builder()
                                        .tag("member")
                                        .summary("signin API")
                                        .description("로그인")
                                        .requestSchema(Schema.schema("Signin.Request"))
                                        .responseSchema(Schema.schema("Signin.Response"))
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
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

    @Test
    @DisplayName("AccessToken 재발급 성공 테스트")
    @WithMockUser
    void reissueSuccessTest() throws Exception {
        //given
        String Authorization = "RefreshToken for Reissue AccessToken";
        given(memberService.reissue(Authorization))
                .willReturn(new ReissueResponse(
                        "AccessToken",
                        "RefreshToken"));
        //when
        //then
        mockMvc.perform(post("/member/reissue")
                        .header("Authorization", Authorization))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("AccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("RefreshToken"))
                .andDo(document("member/reissue",
                        ResourceSnippetParameters.builder()
                                .tag("member")
                                .summary("reissue API")
                                .description("토큰 재발급")
                                .responseSchema(Schema.schema("ReissueResponse"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestHeaders(
                                headerWithName("Authorization").description("Refresh Token"))
                        , responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("AccessToken")
                                , fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("RefreshToken")))
                );
    }

    @Test
    @DisplayName("본인 정보 조회 성공 테스트")
    void userInfoSuccessTest() throws Exception {
        //given
        Product product1 = new Product(1L, "반지", 10000L, 1);
        Product product2 = new Product(2L, "목걸이", 20000L, 2);

        List fundingProductDtoList = Arrays.asList(
                FundingProductDto.builder()
                        .id(1L)
                        .product(product1)
                        .total(5000L)
                        .createdAt(LocalDate.now())
                        .expiredAt(LocalDate.now().plusYears(1))
                        .fundingState(FundingState.ONGOING)
                        .build(),
                FundingProductDto.builder()
                        .id(2L)
                        .product(product2)
                        .total(1000L)
                        .createdAt(LocalDate.now())
                        .expiredAt(LocalDate.now().plusYears(1))
                        .fundingState(FundingState.ONGOING)
                        .build()
        );

        given(memberService.userInfo(any()))
                .willReturn(new UserInfoResponse(
                        "소은",
                        "010-1111-1111",
                        "soni@naver.com",
                        "서울특별시 강남구",
                        LocalDate.of(2000, 01, 28),
                        new ArrayList<>(fundingProductDtoList)
                ));
        //when
        //then
        mockMvc.perform(get("/member/info")
                        .header("Authorization", "Bearer AccessToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("소은"))
                .andExpect(jsonPath("$.phone").value("010-1111-1111"))
                .andExpect(jsonPath("$.email").value("soni@naver.com"))
                .andExpect(jsonPath("$.address").value("서울특별시 강남구"))
                .andExpect(jsonPath("$.birthDay").value("2000-01-28"))
                .andExpect(jsonPath("$.fundingProductList.[0].id").value(1L))
                .andExpect(jsonPath("$.fundingProductList.[0].product.productName").value("반지"))
                .andExpect(jsonPath("$.fundingProductList.[1].id").value(2L))
                .andExpect(jsonPath("$.fundingProductList.[1].product.productName").value("목걸이"))
                .andDo(document("member/info",
                        ResourceSnippetParameters.builder()
                                .tag("member")
                                .summary("user info API")
                                .description("로그인 한 사용자 정보 조회")
                                .responseSchema(Schema.schema("UserInfoResponse"))
                        , requestHeaders(
                                headerWithName("Authorization").description("Bearer AccessToken"))
                        , responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                                , fieldWithPath("birthDay").type(JsonFieldType.STRING).description("생일")
                                , fieldWithPath("fundingProductList[].id").type(JsonFieldType.NUMBER).description("펀딩상품 리스트 아이디")
                                , fieldWithPath("fundingProductList[].product.id").type(JsonFieldType.NUMBER).description("상품 아이디")
                                , fieldWithPath("fundingProductList[].product.productName").type(JsonFieldType.STRING).description("상품 이름")
                                , fieldWithPath("fundingProductList[].product.price").type(JsonFieldType.NUMBER).description("상품 가격")
                                , fieldWithPath("fundingProductList[].product.ranking").type(JsonFieldType.NUMBER).description("상품 순위")
                                , fieldWithPath("fundingProductList[].total").type(JsonFieldType.NUMBER).description("펀딩 총 금액")
                                , fieldWithPath("fundingProductList[].createdAt").type(JsonFieldType.STRING).description("펀딩 시작일")
                                , fieldWithPath("fundingProductList[].expiredAt").type(JsonFieldType.STRING).description("펀딩 종료일")
                                , fieldWithPath("fundingProductList[].fundingState").type(JsonFieldType.STRING).description("펀딩 상태"))

                ));
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void updateSuccessTest() throws Exception {
        //given
        UpdateInfo.Request request =
                new UpdateInfo.Request(
                        "소은", "010-1111-1111", "서울특별시 강남구", LocalDate.of(2000, 01, 28));

        given(memberService.update(any(), any()))
                .willReturn(UpdateInfo.Response.builder()
                        .name("소은")
                        .phone("010-1111-1111")
                        .email("soni@naver.com")
                        .address("서울특별시 강남구")
                        .birthDay(LocalDate.of(2000, 01, 28))
                        .build());


        //when
        //then
        mockMvc.perform(patch("/member/update")
                        .header("Authorization", "Bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("소은"))
                .andExpect(jsonPath("$.phone").value("010-1111-1111"))
                .andExpect(jsonPath("$.email").value("soni@naver.com"))
                .andExpect(jsonPath("$.address").value("서울특별시 강남구"))
                .andExpect(jsonPath("$.birthDay").value("2000-01-28"))
                .andDo(MockMvcRestDocumentationWrapper.document("member/update",
                        ResourceSnippetParameters.builder()
                                .tag("member")
                                .summary("update info API")
                                .description("회원정보 수정")
                                .requestSchema(Schema.schema("UpdateInfo.Request"))
                                .responseSchema(Schema.schema("UpdateInfo.Response"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestHeaders(
                                headerWithName("Authorization").description("Bearer AccessToken"))
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional()
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호").optional()
                                , fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional()
                                , fieldWithPath("birthDay").type(JsonFieldType.STRING).description("생일").optional())
                        , responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("메일")
                                , fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                                , fieldWithPath("birthDay").type(JsonFieldType.STRING).description("생일")))
                );
    }
}