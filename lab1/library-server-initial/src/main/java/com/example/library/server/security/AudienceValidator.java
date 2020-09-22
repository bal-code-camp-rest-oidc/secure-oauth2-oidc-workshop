package com.example.library.server.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    /**
     * Provides the name of teh audience we expect to be set. This name is set using a token mapper for
     * audience in the "library-client" client.
     */
    private static final String REQUIRED_AUDIENCE = "library-service";

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience().contains(REQUIRED_AUDIENCE)) {
            return OAuth2TokenValidatorResult.success();
        } else {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "The required audience '" + REQUIRED_AUDIENCE + "' is missing", null));
        }
    }
}
