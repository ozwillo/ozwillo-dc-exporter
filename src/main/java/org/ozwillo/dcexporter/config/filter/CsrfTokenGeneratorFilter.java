package org.ozwillo.dcexporter.config.filter;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter which adds CSRF information as response headers.
 *
 * Inspired by https://patrickgrimard.com/2014/01/03/spring-security-csrf-protection-in-a-backbone-single-page-app/
 */
public final class CsrfTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        // Spring Security will allow the Token to be included in this header name
        response.setHeader("X-CSRF-HEADER", token.getHeaderName());

        // this is the value of the token to be included as either a header or an HTTP parameter
        response.setHeader("X-CSRF-TOKEN", token.getToken());

        filterChain.doFilter(request, response);
    }
}
