package com.my.expenditure.service.user;

import com.my.expenditure.entity.User;
import com.my.expenditure.model.UserDto;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerNewUserAccount(UserDto accountDto) throws NullPointerException{
        if (emailExists(accountDto.getEmail())) {
            throw new NullPointerException();
        }
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        User user = new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        return userRepository.save(user);
    }

    private boolean emailExists(String email) {
        User user = userRepository.findFirstByEmail(email);

        if (user != null) {
            return true;
        }
        return false;
    }
}
