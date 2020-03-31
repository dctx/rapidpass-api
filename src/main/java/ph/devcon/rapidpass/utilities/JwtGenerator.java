package ph.devcon.rapidpass.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class JwtGenerator {

    private static Logger log = LoggerFactory.getLogger(JwtGenerator.class);

    public static DecodedJWT decodedJWT(String token) {
        return JWT.decode(token);
    }

    private static Map<String, Claim> getAllClaimsFromToken(String token) {
        return decodedJWT(token).getClaims();
    }

    public static String generateToken(Map<String, Object> claims, String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 30)))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public static Date getExpiryDate(String token) {
        return decodedJWT(token).getExpiresAt();
    }

    public static String getSubject(String token) {
        return decodedJWT(token).getSubject();
    }

    public static String getSign(String token) {
        return decodedJWT(token).getSignature();
    }

    public static Boolean validateToken(String token, Map<String, Object> claims, String secret) throws ClaimJwtException {
        String name = getSubject(token);
        Date expirationDate = getExpiryDate(token);
        Boolean isExpired = expirationDate.before(new Date());

        //TODO: Update verification code to use RSA or key
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        Boolean isValid = jwt.getToken().equals(token);

        return (name.equals(claims.get("sub").toString()) && !isExpired && isValid);
    }
}
