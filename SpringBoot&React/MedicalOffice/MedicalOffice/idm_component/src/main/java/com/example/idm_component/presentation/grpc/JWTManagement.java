package com.example.idm_component.presentation.grpc;

import com.example.idm_component.dto.UserAuthorizationDto;
import com.example.idm_component.exceptions.ExpirationJWTException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTManagement {
    private static final long EXPIRE_DURATION = 1* 60 * 60 * 1000;  // 1 ore

    private  String SECRET_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTcwMTY5NjgwNSwiaWF0IjoxNzAxNjk2ODA1fQ.AD7LCCD7BPR3S8_qlIyvBbxw8lyzKPL5CsCjgPaaZaQ";

    public String createJWT(Long id, Long role) {
        return Jwts.builder()
                .setId(id.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }


    public UserAuthorizationDto decodeToken(String token) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            int userId = Integer.parseInt(claims.getId());
            int role = (int)claims.get("role");

            return new UserAuthorizationDto(userId, role);
        }catch (ExpiredJwtException expiredJwtException){
            throw new ExpirationJWTException("Tokenul de autentificare a expirat");
        }
        catch (SignatureException e) {
            throw new SignatureException("Token cu semnatura invalida");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
            throw new Exception("Exceptie la validarea token-ului" );
        }
    }
}
