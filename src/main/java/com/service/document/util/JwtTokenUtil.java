package com.service.document.util;

import com.service.document.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    public Long getUserId(Authentication auth) throws UnauthorizedException {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("No authentication found");
        }
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        return principal.getUserId();
    }

    public Collection<Long> getDeptIds(Authentication auth) throws UnauthorizedException {
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("No authentication found");
        }
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        return principal.getDeptIds();
    }

    public Collection<String> getRoles(Authentication auth) throws UnauthorizedException {
        if (auth == null) {
            throw new UnauthorizedException("No authentication found");
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}