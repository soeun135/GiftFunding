package com.soeun.GiftFunding.security;

import com.soeun.GiftFunding.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;

    private final UserService userService;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generate(String subject, Date expiredAt) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expiredAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }
    public String generateToken(String mail) {
        Claims claims = Jwts.claims().setSubject(mail);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime()
            + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS512,
                this.secretKey)
            .compact();
    }

    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails =
            this.userService.loadUserByUsername(
                this.getUsername(jwt));

        return new UsernamePasswordAuthenticationToken(
            userDetails, "",
            userDetails.getAuthorities());
    }

    public String getUsername(String jwt) {
        return this.parseClaims(jwt).getSubject();
    }
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;

        Claims claims = this.parseClaims(token);
        return !claims.getExpiration()
            .before(new Date());
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
