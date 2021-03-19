package com.java.cloud.training.mock;

import org.assertj.core.util.Maps;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;

public class WithMockOAuth2ScopeSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Scope> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2Scope mockOAuth2Scope) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Jwt jwt = new Jwt("tokenValue", Instant.MIN, Instant.MAX, Maps.newHashMap("key", "value"), Maps.newHashMap("key", "value"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(mockOAuth2Scope.scope());
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Arrays.asList(authority));

        context.setAuthentication(authentication);
        return context;
    }
}