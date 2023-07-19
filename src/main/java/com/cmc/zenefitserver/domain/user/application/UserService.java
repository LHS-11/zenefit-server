package com.cmc.zenefitserver.domain.user.application;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.domain.UserDetail;
import com.cmc.zenefitserver.domain.user.dto.SignUpRequestDto;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmc.zenefitserver.global.error.ErrorCode.DUPLICATE_EMAIL_PROVIDER;
import static com.cmc.zenefitserver.global.error.ErrorCode.DUPLICATE_NICKNAME;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TokenResponseDto signUp(SignUpRequestDto signUpRequestDto){

        // 닉네임 중복 여부 확인
        userRepository.findByNickname(signUpRequestDto.getNickname())
                .ifPresent(user->{
                    throw new BusinessException(DUPLICATE_NICKNAME);
                });

        // 이메일 + provider 중복 여부 확인
        userRepository.findByEmailAndProvider(signUpRequestDto.getEmail(),signUpRequestDto.getProvider())
                .ifPresent(user -> {
                    throw new BusinessException(DUPLICATE_EMAIL_PROVIDER);
                });

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .nickname(signUpRequestDto.getNickname())
                .age(signUpRequestDto.getAge())
                .lastYearIncome(signUpRequestDto.getLastYearIncome())
                .address(signUpRequestDto.getAddress())
                .educationType(signUpRequestDto.getEducationType())
                .jobs(signUpRequestDto.getJobs())
                .provider(signUpRequestDto.getProvider())
                .build();

        UserDetail userDetail = UserDetail.builder()
                .gender(signUpRequestDto.getGender())
                .build();

        user.setUserDetail(userDetail);
        userDetail.setUser(user);

        User savedUser = userRepository.save(user);
        return jwtService.createToken(new TokenRequestDto(savedUser));
    }
}
