package edu.kpi.service.integration.impl;

import edu.kpi.JwtUtils;
import edu.kpi.dto.AccessTokenResponseDto;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CommonGitHubIntegrationService {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_TYPE = "token";
    private static final String JWT_AUTH_HEADER_TYPE = "Bearer";
    private static final String INSTALLATION_ID_PLACEHOLDER = "{installationId}";

    @Value("${github.api.installation.access.token.url}")
    private String githubInstallationAccessTokenUrl;

    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;

    private final Map<String, String> accessTokens = new HashMap<>();

    private String jwt;
    private Date expirationDate;

    public CommonGitHubIntegrationService(final JwtUtils jwtUtils) {

        this.jwtUtils = jwtUtils;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, String> getHeaders(final String installationId) {

        return Optional.ofNullable(accessTokens.get(installationId))
                .map(accessToken -> Map.of(AUTH_HEADER_NAME, AUTH_HEADER_TYPE + " " + accessToken))
                .orElseGet(() -> createAccessToken(installationId));
    }

    private Map<String, String> createAccessToken(final String installationId) {

        final String accessToken = getAccessToken(installationId);

        accessTokens.put(installationId, accessToken);

        return Map.of(AUTH_HEADER_NAME, AUTH_HEADER_TYPE + " " + accessToken);
    }

    private String getAccessToken(final String installationId) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTH_HEADER_NAME, JWT_AUTH_HEADER_TYPE + " " + getIntegrationJwt());

        final HttpEntity<Void> request = new HttpEntity<>(httpHeaders);

        return Optional.of(restTemplate.postForEntity(getServiceUrl(installationId), request, AccessTokenResponseDto.class))
                .filter(resp -> resp.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(AccessTokenResponseDto::getToken)
                .orElseThrow(IllegalArgumentException::new);
    }

    private String getIntegrationJwt() {

        if (Objects.isNull(jwt) || Date.from(Instant.now()).after(expirationDate)) {

            jwt = jwtUtils.generateNewToken();
            expirationDate = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));
        }

        return jwt;
    }

    private String getServiceUrl(final String installationId) {

        return Optional.ofNullable(githubInstallationAccessTokenUrl)
                .orElse(StringUtil.EMPTY_STRING)
                .replace(INSTALLATION_ID_PLACEHOLDER, installationId);
    }
}
