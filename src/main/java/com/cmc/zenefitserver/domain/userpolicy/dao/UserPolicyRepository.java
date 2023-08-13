package com.cmc.zenefitserver.domain.userpolicy.dao;

import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {

    Optional<UserPolicy> findByUser_userIdAndPolicy_Id(Long userId, Long policyId);

}
