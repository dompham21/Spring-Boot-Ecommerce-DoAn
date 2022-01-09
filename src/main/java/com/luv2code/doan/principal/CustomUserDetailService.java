package com.luv2code.doan.principal;

import com.luv2code.doan.entity.User;
import com.luv2code.doan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user =  userRepo.getUserByEmail(email);
            if(user != null) {
                return new UserPrincipal(user);
            }
        }
        catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Cound not find user with email: " + email);
        }
        return null;
    }
}
