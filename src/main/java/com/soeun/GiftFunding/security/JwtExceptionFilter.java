package com.soeun.GiftFunding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.ErrorResponse;
import com.soeun.GiftFunding.exception.TokenException;
import com.soeun.GiftFunding.type.ErrorType;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

        } catch (TokenException e){
            log.info("로그 던지기");
            request.setAttribute("exception", ErrorType.INVALID_TOKEN);
        }
    }
}
