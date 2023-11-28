package com.soeun.GiftFunding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soeun.GiftFunding.dto.ErrorResponse;
import com.soeun.GiftFunding.exception.TokenException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

        } catch (TokenException e){
            response.setStatus(e.getErrorCode().getCode());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = new ObjectMapper().writeValueAsString(
                ErrorResponse.of(e.getErrorCode(), e.getErrorMessage(), e.getErrorCode().getCode()));
            response.getWriter().write(json);
        }
    }
}
