package org.kon.postr.security;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationConverter.class);

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        logger.info("got token: {}", source);

        Collection<? extends GrantedAuthority> authorities = getAuthorities(source);

        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) return List.of();

        @SuppressWarnings("unchecked")
        Map<String, Object> clientRolesMap = (Map<String, Object>) resourceAccess.get("demo-client");
        if (clientRolesMap == null) return List.of();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) clientRolesMap.get("roles");
        if (roles == null) return List.of();

        return roles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    }

}
