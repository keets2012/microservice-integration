package com.blueskykong.demo.filter;

import com.blueskykong.demo.constants.AccessType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserContext implements UserDetails, SecurityContext {

    private static final long serialVersionUID = -5710100626512497575L;

    private UUID userId;

    private String password;

    private String userName;

    private Boolean accountNonLocked = true;

    private Boolean enabled = true;

    private Boolean accountNonExpired = true;

    private Boolean credentialsNonExpired = true;

    private List<UUID> tenantList;

    private Collection<? extends GrantedAuthority> authorities;

    private Authentication authentication;

    private Map<String, String> dataSecurityMap;

    private AccessType accessType;

    public UserContext(UUID userId) {
        this.userId = userId;
    }

    public UserContext() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public Boolean getAccountNonLocked() {
        return this.accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.getAccountNonLocked();
    }

    public String getUsername() {
        return this.userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @JsonIgnore
    public Boolean getAccountNonExpired() {
        return this.accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.getAccountNonExpired();
    }

    @JsonIgnore
    public Boolean getCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.getCredentialsNonExpired();
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public List<UUID> getTenantList() {
        return tenantList;
    }

    public void setTenantList(List<UUID> tenantList) {
        this.tenantList = tenantList;
    }

    /**
     * Return the data security map, which contains the filter conditions for data security,
     * the keys are the type of the filter conditions, such as TENANT or DOCUMENT. the value
     * are the acceptable list for the filter type. usually the value is UUID list.
     *
     * @return
     */
    public Map<String, String> getDataSecurityMap() {
        return dataSecurityMap;
    }

    public void setDataSecurityMap(Map<String, String> dataSecurityMap) {
        this.dataSecurityMap = dataSecurityMap;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }
}

