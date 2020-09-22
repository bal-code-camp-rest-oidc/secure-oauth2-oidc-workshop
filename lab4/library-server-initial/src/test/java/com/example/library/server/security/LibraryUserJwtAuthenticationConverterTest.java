package com.example.library.server.security;

import com.example.library.server.dataaccess.UserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LibraryUserJwtAuthenticationConverterTest {

    private static final String EXPECTED_ROLE = "library_user";

    @Mock
    private LibraryUserDetailsService libraryUserDetailsService;

    @Test
    void convertWithSuccess() {
        Jwt jwt = Jwt.withTokenValue("1234")
                .header("typ", "JWT")
                .claim("sub", "userid")
                .claim("groups", Collections.singletonList(EXPECTED_ROLE))
                .build();

        // provide a mock of the user service: always return a test user
        given(libraryUserDetailsService.loadUserByUsername(any()))
                .willReturn(new LibraryUser(UserBuilder.user().build()));

        LibraryUserJwtAuthenticationConverter componentUnderTest = new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
        AbstractAuthenticationToken authenticationToken = componentUnderTest.convert(jwt);
        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isNotEmpty();
        assertThat(authenticationToken.getAuthorities()
                .iterator().next().getAuthority()).isEqualTo("ROLE_LIBRARY_USER");
    }

    @Test
    void convertWithFailure() {
        Jwt jwt = Jwt.withTokenValue("1234")
                .header("typ", "JWT")
                .claim("sub", "userid")
                .claim("groups", Collections.singletonList(EXPECTED_ROLE))
                .build();

        // provide a mock of the user service: always throw exception
        given(libraryUserDetailsService.loadUserByUsername(any()))
                .willThrow(new UsernameNotFoundException("No user found"));

        LibraryUserJwtAuthenticationConverter componentUnderTest = new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
        assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(
                () -> componentUnderTest.convert(jwt)
        );
    }
}
