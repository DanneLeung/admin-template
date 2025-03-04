package com.xcesys.template.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT令牌提供者，负责生成和验证JWT令牌
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Value("${jwt.header}")
  private String jwtHeader;

  @Value("${jwt.token-prefix}")
  private String jwtTokenPrefix;

  /**
   * 创建JWT令牌
   *
   * @param authentication 用户认证信息
   * @return JWT令牌
   */
  public String createToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

    // 获取用户权限列表
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .claim("id", userPrincipal.getId())
        .claim("companyId", userPrincipal.getCompanyId())
        .claim("authorities", authorities)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 从令牌中获取用户名
   *
   * @param token JWT令牌
   * @return 用户名
   */
  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  /**
   * 从令牌中获取用户ID
   *
   * @param token JWT令牌
   * @return 用户ID
   */
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.get("id", Long.class);
  }

  /**
   * 从令牌中获取用户认证对象
   *
   * @param token JWT令牌
   * @return 用户认证对象
   */
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get("authorities").toString().split(","))
            .filter(auth -> !auth.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    Long companyId = claims.containsKey("companyId") ? Long.valueOf(claims.get("companyId").toString()) : null;
    UserDetails principal = new UserDetailsImpl((Long.valueOf(claims.get("id").toString())), claims.getSubject(), "", Boolean.TRUE, companyId, authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  /**
   * 验证令牌
   *
   * @param token JWT令牌
   * @return 是否有效
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 获取过期时间
   *
   * @param token JWT令牌
   * @return 过期时间
   */
  public Date getExpirationDateFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getExpiration();
  }

  /**
   * 获取JWT令牌前缀
   *
   * @return JWT令牌前缀
   */
  public String getTokenPrefix() {
    return jwtTokenPrefix;
  }

  /**
   * 获取JWT头部名称
   *
   * @return JWT头部名称
   */
  public String getHeader() {
    return jwtHeader;
  }

  /**
   * 获取签名密钥
   *
   * @return 签名密钥
   */
  private Key getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}