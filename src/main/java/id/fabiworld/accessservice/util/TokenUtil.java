package id.fabiworld.accessservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TokenUtil {
    public static final Algorithm ALGORITHM = Algorithm.HMAC256("mY-s3cREt-@cC3$S-kEy".getBytes());
    private static final long CURRENT_MILLIS = System.currentTimeMillis();

    public static String generateAccessToken(String username, String requestUrl, List<?> roles){
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(CURRENT_MILLIS + 10 * 60 * 1000)) // minute * second * millisecond
                .withIssuer(requestUrl)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("roles", roles)
                .sign(ALGORITHM);
    }

    public static String generateRefreshToken(String username, String requestUrl){
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(CURRENT_MILLIS + 30 * 60 * 1000)) // minute * second * millisecond
                .withIssuer(requestUrl)
                .sign(ALGORITHM);
    }

}
