package org.ozwillo.dcexporter.config;

import org.oasis_eu.spring.kernel.security.StaticOpenIdCConfiguration;

public class OpenIdCConfig extends StaticOpenIdCConfiguration {

    @Override
    public boolean requireAuthenticationForPath(String path) {
        return path.contains("/api/") || path.contains("/index.html");
    }
}
