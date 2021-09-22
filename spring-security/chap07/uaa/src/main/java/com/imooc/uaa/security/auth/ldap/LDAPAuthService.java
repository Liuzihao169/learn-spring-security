package com.imooc.uaa.security.auth.ldap;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LDAPAuthService {

    private final LDAPUserRepo userRepository;

    public boolean authenticate(final String username, final String password) {
        return userRepository.findByUsernameAndPassword(username, password).isPresent();
    }

    public List<String> search(final String username) {
        val userList = userRepository.findByUsernameLikeIgnoreCase(username);
        if (userList == null) {
            return Collections.emptyList();
        }

        return userList.stream()
            .map(LDAPUser::getUsername)
            .collect(Collectors.toList());
    }
}
