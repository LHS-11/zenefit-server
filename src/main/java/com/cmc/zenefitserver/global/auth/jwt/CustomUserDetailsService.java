package com.cmc.zenefitserver.global.auth.jwt;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("로그인한 유저 ID = {}",userId);
        return new UserAdapter(userRepository.findByUserId(Long.parseLong(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER)));
    }
}
