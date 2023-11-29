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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    private final GetAuthentication getAuthentication;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = this.resolveTokenFromRequest(request);
        try {
            if (StringUtils.hasText(token) &&
                this.tokenProvider.validateToken(token)) {
                Authentication auth = getAuthentication.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info("[{}] -> {}"
                    , this.tokenProvider.getMail(token)
                    , request.getRequestURI());
            }
        } catch (TokenException e) {
            request.setAttribute("exception", e.getErrorCode());
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
