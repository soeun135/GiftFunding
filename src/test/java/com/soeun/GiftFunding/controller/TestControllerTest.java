package com.soeun.GiftFunding.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.exception.CustomAuthenticationEntryPoint;
import com.soeun.GiftFunding.security.GetAuthentication;
import com.soeun.GiftFunding.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private GetAuthentication getAuthentication;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @TestConfiguration
    private static class TestConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll();
        }
    }
    private final ObjectMapper mapper =
            new ObjectMapper();
    @Test
    void get() throws Exception{
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/user")
        )
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("test-get",
                        ResourceSnippetParameters.builder()
                                .tag("테스트")
                                .summary("get test")
                                .description("get 테스트라고")
                                .responseSchema(Schema.schema("testResponse"))
                                ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("냠")
                        )
                        ));

    }
}