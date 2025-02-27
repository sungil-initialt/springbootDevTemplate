package com.sptek._frameworkWebCore.springSecurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeneralTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secureKey;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public GeneralTokenProvider(@Value("${jwt.base64SecretKey}") String secureKey, @Value("${jwt.tokenValidityInMilliseconds}") long tokenValidityInMilliseconds) {
        this.secureKey = secureKey;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    // Bean의 실제 생성(생성자) 이후 동작
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secureKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String convertAuthenticationToJwt(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 만료 시간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY,authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // 토큰에 정보를 이용해 Authentication (UsernamePasswordAuthenticationToken) 변환
    public Authentication convertJwtToAuthentication(String token){
        // 토큰을 이용하여 claim 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰의 유효성 검사
    public boolean validateJwt(String jwt){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { log.error("Invalid JWT signature"); }
        catch (ExpiredJwtException e) { log.error("Expired JWT token"); }
        catch (UnsupportedJwtException e) { log.error("Unsupported JWT token"); }
        catch (IllegalArgumentException e) { log.error("Invalid JWT token"); }

        return false;
    }
}
