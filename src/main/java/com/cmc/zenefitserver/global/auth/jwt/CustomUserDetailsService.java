package com.cmc.zenefitserver.global.auth.jwt;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("userId = " + userId);
        // Todo : Exception 커스텀
        return new UserAdapter( userRepository.findByUserId(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")));
    }
}
