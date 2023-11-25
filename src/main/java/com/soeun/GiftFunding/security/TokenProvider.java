package com.soeun.GiftFunding.security;

import static com.soeun.GiftFunding.type.ErrorCode.INVALID_TOKEN;
import static com.soeun.GiftFunding.type.ErrorCode.TOKEN_EXPIRED;
import static com.soeun.GiftFunding.type.ErrorCode.USER_DUPLICATED;

import com.soeun.GiftFunding.exception.TokenException;
import com.soeun.GiftFunding.exception.UserException;
import com.soeun.GiftFunding.redis.RefreshToken;
import com.soeun.GiftFunding.redis.RefreshTokenRepository;
import com.soeun.GiftFunding.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60L; //1분

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generateRefreshToken(String mail) {
        String token = Jwts.builder()
            .setSubject(mail)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS512, this.secretKey)
            .compact();

        refreshTokenRepository.save(
            new RefreshToken(token, mail)
        );
        return token;
    }

    public String generateAccessToken(String mail) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime()
            + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
            .setSubject(mail)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS512, this.secretKey)
            .compact();
    }

    public String reIssueAccessToken(String refreshToken) {
        RefreshToken token =
            refreshTokenRepository.findByMail(refreshToken)
                .orElseThrow(
                    () -> new TokenException(INVALID_TOKEN));

        return this.generateAccessToken(token.getMail());
    }

    public String getMail(String jwt) {
        return this.parseClaims(jwt).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new TokenException(INVALID_TOKEN);
        }

        Claims claims = this.parseClaims(token);

        log.info(String.valueOf(claims.getExpiration()));

        if (claims.getExpiration().before(new Date())) {
            throw new TokenException(ErrorCode.TOKEN_EXPIRED);
        }
        return true;
    }

    private Claims parseClaims(String jwt) {
        try {
            return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
