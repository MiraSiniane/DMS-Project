package com.service.document.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Extended principal holding user ID, department IDs, and roles.
 */
public class CustomUserPrincipal implements org.springframework.security.core.Authentication {
    private final Long userId;
    private final Collection<Long> deptIds;
    private final Collection<? extends GrantedAuthority> authorities;
    private boolean authenticated = true;

    public CustomUserPrincipal(Long userId, 
                             Collection<Long> deptIds,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.deptIds = Collections.unmodifiableCollection(deptIds);
        this.authorities = Collections.unmodifiableCollection(authorities);
    }

    // Existing methods
    public Long getUserId() { return userId; }
    public Collection<Long> getDeptIds() { return deptIds; }

    // Spring Security methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public Object getCredentials() { return null; }
    @Override
    public Object getDetails() { return null; }
    @Override
    public Object getPrincipal() { return this; }
    @Override
    public boolean isAuthenticated() { return authenticated; }
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }
    @Override
    public String getName() { return userId.toString(); }
}