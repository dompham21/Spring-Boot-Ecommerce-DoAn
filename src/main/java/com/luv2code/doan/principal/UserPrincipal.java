package com.luv2code.doan.principal;

import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class UserPrincipal implements UserDetails {
    private final Logger log = LoggerFactory.getLogger(UserPrincipal.class);

    private User user;

    public UserPrincipal(User user) {
        super();
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().toUpperCase())));
        }

        return authorities;
    }

    public Integer getId() {
        return user.getId();
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


    public String getName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(user.getId(), that.getId());
    }



    @Override
    public int hashCode() {

        return Objects.hash(user.getId());
    }

    public String getFirstName() {return this.user.getFirstName();}

    public String getFullName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }



}
