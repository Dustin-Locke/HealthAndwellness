package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Base64;
import java.util.Collection;

public class UserPrincipal implements UserDetails {

    private final User user;
    public UserPrincipal(User user) { this.user = user; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return (user.getHash() != null) ? Base64.getEncoder().encodeToString(user.getHash()) : null;
    }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getId() { return (long) user.getId(); }
}
