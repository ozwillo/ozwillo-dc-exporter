package org.ozwillo.dcexporter.config;

import org.oasis_eu.spring.config.OasisSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends OasisSecurityConfiguration {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(oasisAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler(logoutHandler())
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint()).and()
            .addFilterAfter(oasisExceptionTranslationFilter(authenticationEntryPoint()), ExceptionTranslationFilter.class);
    }
}
