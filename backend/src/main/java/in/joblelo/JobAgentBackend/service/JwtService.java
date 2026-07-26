package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.utils.UserUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @org.springframework.beans.factory.annotation.Value("${jwt.secret.key}")
    private String secretKey;
    private final String accessType = "ACCESS";
    private final String refreshType = "REFRESH";
    private final String ISSUER = "JobLelo";


    @Autowired
    private UserUtil userUtil;

    private final long ACCESS_TOKEN_EXPIRATION = 60L * 60 * 1000; // 60 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 30L * 24 * 60 * 60 * 1000; // 30 days

    private Key signingKey;

    @PostConstruct
    private void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("SECRET_KEY system property is not set");
        }
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccess(String userId, String email) {
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(userId)
                .claim("email", email)
                .claim("type", accessType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public String generateRefresh(String userId, String email) {
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(userId)
                .claim("email", email)
                .claim("type", refreshType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            boolean isUserValid = userUtil.isValidUserId(parseClaims(refreshToken).getSubject());
            if (!isUserValid) {
                return false;
            }
            Claims claims = parseClaims(refreshToken);
            String type = claims.get("type", String.class);
            Date exp = claims.getExpiration();
            String issuer = claims.getIssuer();
            return refreshType.equals(type) && exp != null && exp.after(new Date()) && ISSUER.equals(issuer);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            boolean isUserValid = userUtil.isValidUserId(parseClaims(accessToken).getSubject());
            if (!isUserValid) {
                return false;
            }
            Claims claims = parseClaims(accessToken);
            String type = claims.get("type", String.class);
            Date exp = claims.getExpiration();
            String issuer = claims.getIssuer();
            return accessType.equals(type) && exp != null && exp.after(new Date()) && ISSUER.equals(issuer);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractClaim(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
