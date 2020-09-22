package com.example.library.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    /**
     * Defines teh constant for the key that we use to access the property
     * "spring.security.oauth2.client.registration.keycloak" from application.yml
     */
    private static final String CLIENT_SECTION = "keycloak";

    @Bean
    WebClient webClient(
            ClientRegistrationRepository clientRegistrations,
            OAuth2AuthorizedClientRepository authorizedClients) {

        // create a filter to automatically add access tokens to all requests and initiates the
        // authorization grant flow if no valid access token is available
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2ClientFilter =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations, authorizedClients);
        oauth2ClientFilter.setDefaultOAuth2AuthorizedClient(true);
        oauth2ClientFilter.setDefaultClientRegistrationId(CLIENT_SECTION);
        return WebClient.builder().apply(oauth2ClientFilter.oauth2Configuration()).build();

    }
}
