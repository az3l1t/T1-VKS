package net.az3l1t.slots_server.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final String SECRET_KEY = "VuV8BeraqKtZMLyKbXXeUiWqoaH3/ynH9RM4olmZUAeTFu8/LsdCiA49Wxru3S2WTC4gcq7KrYGv3SJD91TTQNdPLHN/rUAsy6XCn5x19DnMrmr5c6cP5/0kjfDjhg6B9CJLnRf7smrlkfvu4ybEvxd2HA+b+PpUtPrpiNznskBcay+tmWxSloFxidaN/1xHbPl/I6i/hP4UvGxJJONQpaTEWNJi+Oixt/3UdQWuOU4U7sAIDtFSSza5DcE6B1HXIAoc+Q0FQ6/V6zRqRZt3hcwFRHMWEmX+t8ZuYxvoNfEeD1FN7F2xlkczd4Fjxyh5TCEx03/RcxEFRiRZR/vV4iHasXUYE7uhKz3d+UuhqSQ=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(role -> "ROLE_" + role) // добавление префикса
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}