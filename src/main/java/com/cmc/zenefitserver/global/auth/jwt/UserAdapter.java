package com.cmc.zenefitserver.global.auth.jwt;

import com.cmc.zenefitserver.domain.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Getter
public class UserAdapter extends org.springframework.security.core.userdetails.User {

    private User user;

    public UserAdapter(User user) {
        super(user.getNickname(), "", Collections.singleton(new SimpleGrantedAuthority("USER")));
        this.user=user;
    }
}
