package com.my.expenditure.service;

import com.my.expenditure.entity.User;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userName) {
        User user = userRepository.findFirstByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException(userName);
        }
        return new MyUserPrincipal(user);
    }
}
