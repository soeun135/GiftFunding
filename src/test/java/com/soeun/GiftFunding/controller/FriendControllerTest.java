package com.soeun.GiftFunding.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.config.SecurityConfig;
import com.soeun.GiftFunding.domain.friend.dto.FriendFundingProduct;
import com.soeun.GiftFunding.domain.friend.dto.FriendList;
import com.soeun.GiftFunding.domain.friend.dto.FriendRequest;
import com.soeun.GiftFunding.domain.friend.dto.FriendRequestProcess;
import com.soeun.GiftFunding.domain.friend.controller.FriendController;
import com.soeun.GiftFunding.dto.*;
import com.soeun.GiftFunding.domain.product.entity.Product;
import com.soeun.GiftFunding.mock.WithMockUser;
import com.soeun.GiftFunding.security.JwtAuthenticationFilter;
import com.soeun.GiftFunding.domain.friend.service.FriendServiceImpl;
import com.soeun.GiftFunding.type.FriendState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.soeun.GiftFunding.type.FriendState.ACCEPT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FriendController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
@AutoConfigureRestDocs
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
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/friend/request")
                        .header("Authorization", "Bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.message").value("님에게 친구요청을 보냈습니다."))
                .andDo(document("friend/request",
                                ResourceSnippetParameters.builder()
                                        .tag("friend")
                                        .summary("friend request API")
                                        .description("친구 요청")
                                        .requestSchema(Schema.schema("FriendRequest.Request"))
                                        .responseSchema(Schema.schema("FriendRequest.Response"))
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , requestHeaders(
                                        headerWithName("Authorization").description("Bearer AccessToken"))
                                , requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("친구 요청 걸 상대방 이메일"))
                                , responseFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("친구 요청 걸 상대방 이메일"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"))
                        )
                );
    }

    @Test
    @WithMockUser
    @DisplayName("친구 목록 리스트 조회 테스트")
    void friendListTest() throws Exception {
        //given
        FriendState friendState = ACCEPT;
        Pageable pageable = PageRequest.of(0, 10);

        List<FriendList> friendList = Arrays.asList(
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
        mockMvc.perform(RestDocumentationRequestBuilders.get("/friend")
                        .header("Authorization", "Bearer AccessToken")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .param("friendState", "ACCEPT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].memberName").value("버니"))
                .andExpect(jsonPath("$.content.[0].memberEmail").value("buni@naver.com"))
                .andExpect(jsonPath("$.content.[1].memberName").value("벅스"))
                .andExpect(jsonPath("$.content.[1].memberEmail").value("bucks@naver.com"))
                .andDo(MockMvcRestDocumentationWrapper.document("friend/list"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(ResourceSnippetParameters.builder()
                                        .tag("friend")
                                        .summary("friend list API")
                                        .description("친구 목록 조회 ")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("Bearer Access Token"))
                                        .requestParameters(
                                                parameterWithName("friendState").description("친구 상태")
                                                , parameterWithName("size").description("한 페이지 당 노출될 항목 수")
                                                , parameterWithName("page").description("현재 페이지")
                                        )
                                        .responseSchema(Schema.schema("FriendList"))
                                        .responseFields(
                                                fieldWithPath("content[].memberName").description("친구 이름")
                                                , fieldWithPath("content[].memberEmail").description("친구 이메일")
                                                , fieldWithPath("content[].createdAt").description("항목 생성 일시")

                                                , fieldWithPath("pageable").type(JsonFieldType.STRING).description("페이지 정보")
                                                , fieldWithPath("totalElements").description("테이블 총 데이터 갯수")
                                                , fieldWithPath("totalPages").description("전체 페이지 갯수")
                                                , fieldWithPath("last").description("마지막 페이지인지 여부")
                                                , fieldWithPath("size").description("한 페이지당 조회할 데이터 갯수")
                                                , fieldWithPath("number").description("현재 페이지 번호")

                                                , fieldWithPath("sort.empty").description("데이터 비었는지 여부")
                                                , fieldWithPath("sort.sorted").description("정렬 여부")
                                                , fieldWithPath("sort.unsorted").description("정렬 안 됐는지 여부")

                                                , fieldWithPath("first").description("첫 번째 페이지인지 여부")
                                                , fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 갯수")
                                                , fieldWithPath("empty").description("데이터 비었는지 여부"))
                                        .build()
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    @DisplayName("친구 요청 처리 성공 테스트")
    void requestProcessSuccessTest() throws Exception {
        //given
        FriendRequestProcess.Request request =
                new FriendRequestProcess.Request("buni@naver.com", ACCEPT);

        given(friendService.requestProcess(any(), any()))
                .willReturn(FriendRequestProcess.Response
                        .builder()
                        .email("buni@naver.com")
                        .message("님의 친구요청 상태를 업데이트 했습니다.")
                        .build());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(("/friend/process"))
                        .header("Authorization", "Bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("buni@naver.com"))
                .andExpect(jsonPath("$.message").value("님의 친구요청 상태를 업데이트 했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("/friend/process",
                                ResourceSnippetParameters.builder()
                                        .tag("friend")
                                        .summary("friend process API")
                                        .description("친구 요청 처리")
                                        .requestSchema(Schema.schema("FriendRequestProcess.Request"))
                                        .responseSchema(Schema.schema("FriendRequestProcess.Response"))
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , requestHeaders(
                                        headerWithName("Authorization").description("Bearer Access Token"))
                                , requestFields(
                                        fieldWithPath("email").description("친구 이메일")
                                        , fieldWithPath("state").description("요청 상태"))
                                , responseFields(
                                        fieldWithPath("email").description("친구 이메일")
                                        , fieldWithPath("message").description("메세지")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    @DisplayName("친구의 펀딩상품 조회 성공 테스트")
    void friendProductSuccessTest() throws Exception {
        //given
        Product product1 = new Product(1L, "반지", 10000L, 1);
        Product product2 = new Product(2L, "목걸이", 20000L, 2);

        List fundingProductDtoList = Arrays.asList(
                FundingProductDto.builder()
                        .id(1L)
                        .product(product1)
                        .total(5000L)
                        .build(),
                FundingProductDto.builder()
                        .id(2L)
                        .product(product2)
                        .total(1000L)
                        .build()
        );
        FriendFundingProduct friendFundingProduct =
                FriendFundingProduct.builder()
                        .name("버니")
                        .phone("010-1111-1111")
                        .email("buni@naver.com")
                        .birthDay(LocalDate.of(2013, 07, 31))
                        .fundingProductList(new PageImpl<>(fundingProductDtoList))
                        .build();
        given(friendService.friendProduct(any(), any(), any()))
                .willReturn(friendFundingProduct);

        Long friendId = 2L;
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/friend/funding-product/{friendId}", friendId)
                        .header("Authorization", "Bearer AccessToken")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("버니"))
                .andExpect(jsonPath("$.phone").value("010-1111-1111"))
                .andExpect(jsonPath("$.email").value("buni@naver.com"))
                .andExpect(jsonPath("$.birthDay").value("2013-07-31"))
                .andExpect(jsonPath("$.fundingProductList.content.[0].product.productName").value("반지"))
                .andExpect(jsonPath("$.fundingProductList.content.[1].product.productName").value("목걸이"))
                .andDo(MockMvcRestDocumentationWrapper.document("/friend/funding-product/{id}"
                        , preprocessResponse(prettyPrint())
                        , resource(ResourceSnippetParameters.builder()
                                .tag("friend")
                                .summary("friend fundingProduct API")
                                .description("친구의 펀딩 상품 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("Bearer Access Token")
                                )
                                .pathParameters(
                                        parameterWithName("friendId").description("친구 아이디")
                                )
                                .requestParameters(
                                        parameterWithName("size").description("한 페이지 당 노출될 항목 수")
                                        , parameterWithName("page").description("현재 페이지")
                                )
                                .responseSchema(Schema.schema("FriendFundingProduct"))
                                .responseFields(
                                        fieldWithPath("name").description("친구 이름")
                                        , fieldWithPath("phone").description("친구 전화번호")
                                        , fieldWithPath("email").description("친구 이메일")
                                        , fieldWithPath("birthDay").description("친구 생일")

                                        , fieldWithPath("fundingProductList.content[].id").description("펀딩 상품 아이디")
                                        , fieldWithPath("fundingProductList.content[].product.id").description("상품 아이디")
                                        , fieldWithPath("fundingProductList.content[].product.productName").description("상품 이름")
                                        , fieldWithPath("fundingProductList.content[].product.price").description("상품 가격")
                                        , fieldWithPath("fundingProductList.content[].product.ranking").description("상품 순위")
                                        , fieldWithPath("fundingProductList.content[].total").description("펀딩 총액")
                                        , fieldWithPath("fundingProductList.content[].createdAt").description("펀딩 생성일")
                                        , fieldWithPath("fundingProductList.content[].expiredAt").description("펀딩 종료일")
                                        , fieldWithPath("fundingProductList.content[].fundingState").description("펀딩 상태")

                                        , fieldWithPath("fundingProductList.pageable").type(JsonFieldType.STRING).description("페이지 정보")
                                        , fieldWithPath("fundingProductList.totalElements").description("테이블 총 데이터 갯수")
                                        , fieldWithPath("fundingProductList.totalPages").description("전체 페이지 갯수")
                                        , fieldWithPath("fundingProductList.last").description("마지막 페이지인지 여부")
                                        , fieldWithPath("fundingProductList.size").description("한 페이지당 조회할 데이터 갯수")
                                        , fieldWithPath("fundingProductList.number").description("현재 페이지 번호")

                                        , fieldWithPath("fundingProductList.sort.empty").description("데이터 비었는지 여부")
                                        , fieldWithPath("fundingProductList.sort.sorted").description("정렬 여부")
                                        , fieldWithPath("fundingProductList.sort.unsorted").description("정렬 안 됐는지 여부")

                                        , fieldWithPath("fundingProductList.first").description("첫 번째 페이지인지 여부")
                                        , fieldWithPath("fundingProductList.numberOfElements").description("요청 페이지에서 조회된 데이터 갯수")
                                        , fieldWithPath("fundingProductList.empty").description("데이터 비었는지 여부"))
                                .build())
                ));

    }

    @Test
    @DisplayName("친구 요청 동시성 테스트")
    void redissonLockTest() {
        //given

        //when
        //then
    }
}