package com.example.android.taxiokay.ui.login;

import java.util.Date;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;

public class JWT {
    private static String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    private String createJWT(String username) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        String apiKeySecretBytes = new String(Base64.encodeBase64(SECRET_KEY.getBytes()));

        JwtBuilder builder = Jwts.builder()
                .setId("UserTOK")
                .setIssuer("TaxiOkay")
                .setSubject(username)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, apiKeySecretBytes);

        long expMillis = nowMillis + 100000;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);

        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(new String(Base64.encodeBase64(SECRET_KEY.getBytes())))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
}
