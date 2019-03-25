package com.abb.bye.web.oauth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
@Service("userDetailsService")
public class AuthorizationUserManager implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return User.withUsername("user_1").password("$2a$10$XnjRZUBulCybgvtcBehnreFfJzqRokwh3iRzQCeRCO2yENZz5qXiu").authorities("USER").build();
    }
}
