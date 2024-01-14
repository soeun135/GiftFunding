package com.soeun.GiftFunding.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private FundingProductRepository fundingProductRepository;
//
//    @Mock
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @InjectMocks
//    private MemberServiceImpl memberService;
//
//    @Test
//    void successSignup() {
//        //given
//        Request request = Request.builder()
//            .name("soni")
//            .phone("01011111111")
//            .email("test@naver.com")
//            .password("1111") //인코딩해서 넣어야함
//            .address("경기도")
//            .birthDay("2000-01-28")
//            .build();
//
//        given(memberRepository.save(any()))
//            .willReturn(Member
//                .builder()
//                .name("soni")
//                .phone("01011111111")
//                .email("test@naver.com")
//                .password("1111") //인코딩해서 넣어야함
//                .address("경기도")
//                .birthDay(LocalDate.parse("2000-01-28"))
//                .build());
//        //when
//        ArgumentCaptor<Member> captor =
//            ArgumentCaptor.forClass(Member.class);
//
//        Signup.Response response = memberService.signUp(request);
//
//        //then
//        verify(memberRepository, times(1))
//            .save(captor.capture());
//        assertEquals(request.getName(),captor.getValue().getName());
//        assertEquals("님 회원가입이 완료되었습니다.", response.getMessage());
//    }
//
//     @Test
//     @DisplayName("로그인 성공 테스트")
//     void successSignin() {
//         //given
//         //given()
//         //when
//         //then
//      }
}