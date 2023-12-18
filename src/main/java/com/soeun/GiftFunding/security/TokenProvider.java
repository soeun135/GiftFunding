package com.soeun.GiftFunding.security;


import com.soeun.GiftFunding.exception.TokenException;
import com.soeun.GiftFunding.redis.RefreshToken;
import com.soeun.GiftFunding.redis.RefreshTokenRepository;
import com.soeun.GiftFunding.type.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60L * 60L; //1시간
    private static final String TOKEN_TYPE = "token_type";
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generateRefreshToken(String mail) {
        Claims claims = Jwts.claims().setSubject(mail);
        claims.put(TOKEN_TYPE, "RTK");

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS512, this.secretKey)
            .compact();

        refreshTokenRepository.save(
            new RefreshToken(token, mail)
        );
        return token;
    }

    public String generateAccessToken(String mail) {
        Claims claims = Jwts.claims().setSubject(mail);
        claims.put(TOKEN_TYPE, "ATK");

        Date now = new Date();
        Date expiredDate = new Date(now.getTime()
            + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS512, this.secretKey)
            .compact();
    }

    public String reIssueAccessToken(String refreshToken) {
        RefreshToken token =
            refreshTokenRepository.findByMail(refreshToken)
                .orElseThrow(() ->
                    new TokenException(ErrorType.REFRESHTOKEN_EXPIRED));

        return this.generateAccessToken(token.getMail());
    }
    public String getTokenType(Claims claims) {
        return (String) claims.get(TOKEN_TYPE);
    }

    public String getMail(String jwt) {
        return this.parseClaims(jwt).getSubject();
    }

    public Claims parseClaims(String jwt) {
        try {
            return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}