/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class JwtGenerator {

    // jonas - maybe move this to commons?

    private static Logger log = LoggerFactory.getLogger(JwtGenerator.class);

    public static DecodedJWT decodedJWT(String token) {
        return JWT.decode(token);
    }

    private static Map<String, Claim> getAllClaimsFromToken(String token) {
        return decodedJWT(token).getClaims();
    }

    public static String generateToken(Map<String, Object> claims, String secret) {
        final Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // todo jwt expiration as configurable
                .setExpiration(now.getTime())
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


    public static Map<String, Object> claimsToMap(String token) {
        return JwtGenerator.decodedJWT(token)
                // [string,claims] to [string,obj]
                .getClaims().entrySet()
                .stream()
                .filter(entry -> entry.getValue().asString() != null)
                .collect(toMap(Map.Entry::getKey,
                        entry -> (Object) entry.getValue().asString()));
    }
}
