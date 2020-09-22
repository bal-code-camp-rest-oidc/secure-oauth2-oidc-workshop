package com.example.library.client.credentials.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("!testing")
@Configuration
public class WebClientConfiguration {

    private static final String REGISTRATION_ID = "library_client";

    @Bean
    ReactiveClientRegistrationRepository clientRegistrations() {
        // Configure as OAuth 2/OIDC client with client credentials
        ClientRegistration clientRegistration = ClientRegistrations
                .fromOidcIssuerLocation("http://localhost:8080/auth/realms/workshop")
                .registrationId(REGISTRATION_ID)
                .clientId("library-client")
                .clientSecret("9584640c-3804-4dcd-997b-93593cfb9ea7")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }

    @Bean
    ReactiveOAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations());
    }

    @Bean
    WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations, ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        // Configure web client to send bearer access token.
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, authorizedClientService));
        oauth.setDefaultClientRegistrationId(REGISTRATION_ID);
        return WebClient.builder()
                .filter(oauth)
                .build();
    }
}
