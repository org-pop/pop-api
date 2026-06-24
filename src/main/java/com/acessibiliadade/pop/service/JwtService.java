package com.acessibiliadade.pop.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Setando o tempo de vida do token para 24 horas
    private static final long EXPIRATION_TIME = 86400000;

    //Extrai o email do token
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //Extrai uma informação específica do token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Gera um token para um usuário
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    // Cria o token com as claims e a subject
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida se o token é valido para um determinado email
    public boolean validateToken(String token, String email){
        final String extractEmail = extractEmail(token);
        return (extractEmail.equals(email) && !isTokenExpired(token));
    }

    // Verifica se o boolean expirou
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Extrai a data de expiração
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrai todas as claims do token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Gera a chave de assinatura a partir da SECRET_KEY
    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
