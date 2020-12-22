package edu.kpi;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.util.internal.StringUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final String GITHUB_APP_ID_KEY = "github.app.id";
    private static final String GITHUB_SECRET_KEY_PATH = "github.app.key.path";
    private static final String GITHUB_JWT_ISSUER_KEY = "iss";

    private final Environment environment;

    public JwtUtils(final Environment environment) {

        this.environment = environment;
    }

    public String generateNewToken() {

        String keyPem = getSecret();

        keyPem = keyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyDER = Base64.getDecoder().decode(keyPem);

        KeyFactory keyFactory = null;
        PrivateKey privateKey = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        Instant now = Instant.now();

        return Jwts.builder()
                .claim(GITHUB_JWT_ISSUER_KEY, environment.getProperty(GITHUB_APP_ID_KEY))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                .signWith(
                        SignatureAlgorithm.RS256,
                        privateKey
                )
                .compact();
    }

    private String getSecret() {

        final URL fileUrl = Optional.ofNullable(getClass().getClassLoader()
                .getResource(environment.getProperty(GITHUB_SECRET_KEY_PATH)))
                .orElseThrow(IllegalArgumentException::new);

        try {

            Path path = Paths.get(fileUrl.toURI());

            return Files.lines(path).collect(Collectors.joining());

        } catch (Exception e) {

            e.printStackTrace();
        }

        return StringUtil.EMPTY_STRING;
    }
}
