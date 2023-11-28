package com.soeun.GiftFunding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.ErrorResponse;
import com.soeun.GiftFunding.exception.TokenException;
import com.soeun.GiftFunding.type.ErrorType;
import java.io.IOException;
import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e){
            log.info("로극 던저");
            request.setAttribute("exception", ErrorType.TOKEN_EXPIRED);
        }
    }
}