package com.soeun.GiftFunding.security;

import com.soeun.GiftFunding.exception.TokenException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            log.info("필터 내 예외처리");
            request.setAttribute("exception", e.getErrorCode());
        }
    }
}
