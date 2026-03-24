package com.example.httpnode.service;

import com.example.httpnode.dto.BlackListDTO;
import com.example.httpnode.dto.UserAuthorizationDto;
import com.example.httpnode.dto.UserDto;
import com.example.httpnode.dto.UserIdentityDto;
import com.example.httpnode.exception.UnauthorizedException;
import com.example.httpnode.other.CustomPrinter;
import com.example.httpnode.repository.BlacklistRepository;
import com.example.httpnode.repository.IDMRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Service
@AllArgsConstructor
public class TokenService {
    private static String SECRET_KEY;
    private IDMRepository idmRepository;
    private BlacklistRepository blacklistRepository;

    public void logout(String token){
        this.blacklistRepository.save(new BlackListDTO(token));
    }

    private static String SecretKeySingleton() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private static String getInstance() throws NoSuchAlgorithmException {
        if (SECRET_KEY == null) {
            synchronized (String.class) {
                SECRET_KEY = SecretKeySingleton();
            }
        }
        return SECRET_KEY;
    }

    public String createToken(String id, String role) throws NoSuchAlgorithmException {
        long currentTimeMillis = System.currentTimeMillis();

        long hour = 1;
        long expirationTimeMillis = currentTimeMillis + (hour * 60 * 60 * 1000);
        return Jwts.builder()
                .setId(id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationTimeMillis) )
                .signWith(SignatureAlgorithm.HS512,   getInstance())
                .compact();
    }


    public UserAuthorizationDto decodeToken(String token) throws UnauthorizedException {

        Iterable<BlackListDTO> list = this.blacklistRepository.findAll();

        for (BlackListDTO dto : list) {
            if(Objects.equals(token, dto.getToken())){
                CustomPrinter.printNormal(dto.getToken());
                throw new UnauthorizedException("Excepție la validarea token-uli, token in blacklist!","Exception during token validation");
            }
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getInstance())
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.getId();
            String role = (String) claims.get("role");
            Optional<UserDto> user = this.idmRepository.findById(userId);
            if(user.isPresent())
                return new UserAuthorizationDto(userId,role,user.get().getEmail());
            else
            {
                this.blacklistRepository.save(new BlackListDTO(token));
                throw new UnauthorizedException("Nu există un utilizator cu acest id","There is no user with this ID.");
            }
        } catch (SignatureException e) {
            this.blacklistRepository.save(new BlackListDTO(token));
            throw new UnauthorizedException("Token cu semnătură invalidă!","Token with invalid signature");
        } catch (Exception e) {
            CustomPrinter.printErr(e.getMessage());
            this.blacklistRepository.save(new BlackListDTO(token));
            throw new UnauthorizedException("Excepție la validarea token-uli!","Exception during token validation");
        }
    }
}
