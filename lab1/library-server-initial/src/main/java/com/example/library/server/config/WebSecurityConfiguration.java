package com.example.library.server.config;

import com.example.library.server.security.AudienceValidator;
import com.example.library.server.security.LibraryUserDetailsService;
import com.example.library.server.security.LibraryUserJwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Provides injected properties for OAUTH2.
     */
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    /**
     * Provides teh service we use to map existing users to users specified in teh JWT token to verify within
     * authentication.
     */
    private final LibraryUserDetailsService libraryUserDetailsService;

    /**
     * Default constructor called by spring boot security.
     *
     * @param oAuth2ResourceServerProperties injected properties for OAUTH2
     * @param libraryUserDetailsService      teh service we use to map existing users to users specified in teh JWT token to verify within
     *                                       authentication.
     */
    public WebSecurityConfiguration(OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                                    LibraryUserDetailsService libraryUserDetailsService) {
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.libraryUserDetailsService = libraryUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //we set sessions to stateless
        //disable cross site request forgery (OWASP) (debugging purposes)
        //and authenticate all requests fully using oauth2
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(withDefaults())
                .csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(libraryUserJwtAuthenticationConverter());
    }

    /**
     * Provides an injected decoder for JWT tokens.
     *
     * @return the initialized JWT token checking issuer and audience.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        //create a default decoder
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri())
                        .build();

        //set validator for issuer and audience
        OAuth2TokenValidator<Jwt> issuerAudienceValidator =
                new DelegatingOAuth2TokenValidator<Jwt>(
                        JwtValidators.createDefaultWithIssuer(
                                oAuth2ResourceServerProperties.getJwt().getIssuerUri()), new AudienceValidator());
        jwtDecoder.setJwtValidator(issuerAudienceValidator);

        return jwtDecoder;
    }

    /**
     * Provides an injected converter for specified user (by eMail) in the JWT token to a user in our user service.
     *
     * @return the converter ready to be used.
     */
    @Bean
    LibraryUserJwtAuthenticationConverter libraryUserJwtAuthenticationConverter() {
        return new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
    }

    /**
     * Provides a password encoder - using JWT no longer used but kept for backward compatibility (and not having to
     * refactor code completely).
     *
     * @return the password encoder, currently not used.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Provides an injected configuration source we use for Cross-Origin Resource Sharing (needed to be whitelisted esp.
     * for REST based systems.
     *
     * @return the CORS configuration source.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:4200", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
