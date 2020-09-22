package com.example.library.server.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibraryUserJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";

    private final LibraryUserDetailsService libraryUserDetailsService;

    public LibraryUserJwtAuthenticationConverter(
            LibraryUserDetailsService libraryUserDetailsService) {
        this.libraryUserDetailsService = libraryUserDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        //extract all roles/ authorities
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        //extract the e-mail from JWT and try to resolve with user service
        //as result provide an auth token with user and his roles
        return Optional.ofNullable(
                libraryUserDetailsService.loadUserByUsername(jwt.getClaimAsString("email")))
                .map(username -> new UsernamePasswordAuthenticationToken(username, "n/a", authorities))
                .orElseThrow(() -> new BadCredentialsException("No user found"));
    }

    /**
     * Internal method used to provide all authorities (aka roles) provided in the JWT token:
     * taking all groups we add the role prefix (required by Spring)
     *
     * @return collection of authorities (aka roles) in the JWT token.
     */

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return this.getGroups(jwt).stream()
                .map(authority -> ROLE_PREFIX + authority.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Internal method used to provide all groups provided in the JWT token.
     *
     * @return collection of groups in the JWT token.
     */
    @SuppressWarnings("unchecked")
    private Collection<String> getGroups(Jwt jwt) {
        Object groups = jwt.getClaims().get(GROUPS_CLAIM);
        if (groups instanceof Collection) {
            return (Collection<String>) groups;
        }

        return Collections.emptyList();
    }
}
