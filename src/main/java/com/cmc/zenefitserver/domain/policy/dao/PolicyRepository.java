package com.cmc.zenefitserver.domain.policy.dao;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy,Long> {

    Optional<Policy> findByBizId(String bizId);


}
