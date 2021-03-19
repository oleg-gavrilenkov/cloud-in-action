package com.java.cloud.training.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.java.cloud.training.security.Scopes.FULL_ACCESS_SCOPE;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
           .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/swagger-api-docs.html", "/swagger-ui/**", "/api-docs/**", "/actuator/health").permitAll()
                .antMatchers(HttpMethod.POST).hasAuthority(FULL_ACCESS_SCOPE)
                .antMatchers(HttpMethod.DELETE).hasAuthority(FULL_ACCESS_SCOPE)
                .antMatchers(HttpMethod.PUT).hasAuthority(FULL_ACCESS_SCOPE)
                .anyRequest().authenticated()
           .and()
                .oauth2ResourceServer().jwt();
        http.csrf().disable();
    }

}
