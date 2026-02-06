package com.mahmoud.todo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class JWTService {

    private static final String SECRET_KEY = "EE8iAdXEPltq91qqUhtZMjORdzMQF4uLpwG9bu6qd4e";
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);



        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration((expiryDate))
                .signWith(getSigningKey())
                .compact();

    }


    public String extractUserName(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public void validateToken(String token, UserDetails appUserDetails) {
        Claims claims=parseClaims(token);
        String username=claims.getSubject();
        Date expiration=claims.getExpiration();

        if(!username.equals(appUserDetails.getUsername()) || username==null ){
            throw new BadCredentialsException("Invalid username");
        }
        if(expiration == null || expiration.before(new Date())){
            throw new BadCredentialsException("Token expired");
        }

    }

    public Claims parseClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getPayload();
        }
        catch(Exception ex){
            throw new BadCredentialsException("Invalid token",ex);

        }
    }

}
