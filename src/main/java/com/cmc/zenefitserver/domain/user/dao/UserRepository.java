package com.cmc.zenefitserver.domain.user.dao;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.auth.ProviderType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = {"jobs"})
    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmailAndProvider(String email, ProviderType provider);

    Optional<User> findByNickname(String nickname);

}
