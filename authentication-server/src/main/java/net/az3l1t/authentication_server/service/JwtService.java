package net.az3l1t.authentication_server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.security.Keys;
import net.az3l1t.authentication_server.core.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private String SECRET_KEY = "VuV8BeraqKtZMLyKbXXeUiWqoaH3/ynH9RM4olmZUAeTFu8/LsdCiA49Wxru3S2WTC4gcq7KrYGv3SJD91TTQNdPLHN/rUAsy6XCn5x19DnMrmr5c6cP5/0kjfDjhg6B9CJLnRf7smrlkfvu4ybEvxd2HA+b+PpUtPrpiNznskBcay+tmWxSloFxidaN/1xHbPl/I6i/hP4UvGxJJONQpaTEWNJi+Oixt/3UdQWuOU4U7sAIDtFSSza5DcE6B1HXIAoc+Q0FQ6/V6zRqRZt3hcwFRHMWEmX+t8ZuYxvoNfEeD1FN7F2xlkczd4Fjxyh5TCEx03/RcxEFRiRZR/vV4iHasXUYE7uhKz3d+UuhqSQ=";

    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return ((username.equals(user.getUsername())) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
