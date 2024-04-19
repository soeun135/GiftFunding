package com.soeun.GiftFunding.security;


import com.soeun.GiftFunding.exception.MemberException;
import com.soeun.GiftFunding.exception.TokenException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.soeun.GiftFunding.type.ErrorType.*;

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
            if (!StringUtils.hasText(token)) {
                throw new TokenException(INVALID_TOKEN);
            }

            Claims claims = tokenProvider.parseClaims(token);
            String requestURI = request.getRequestURI();
            String tokenType = tokenProvider.getTokenType(claims);

            if ("RTK".equals(tokenType) &&
                !"/member/reissue".equals(requestURI)) {
                throw new TokenException(INVALID_TOKEN);
            }

            if ("ATK".equals(tokenType)) {
                if (claims.getExpiration().before(new Date())) {
                    log.info("토큰 만료 예외 발생");
                    throw new TokenException(TOKEN_EXPIRED);
                }
                if (ObjectUtils.isEmpty(tokenProvider.getMail(token))) {
                    throw new MemberException(USER_NOT_FOUND);
                }
            }
            Authentication auth = getAuthentication.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("[{}] -> {}"
                , this.tokenProvider.getMail(token)
                , request.getRequestURI());

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
