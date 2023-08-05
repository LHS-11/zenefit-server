package com.cmc.zenefitserver.domain.userpolicy.dao;

import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPolicyRepository extends JpaRepository<UserPolicy,Long> {
}
