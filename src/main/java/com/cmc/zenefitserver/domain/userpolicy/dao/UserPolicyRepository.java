package com.cmc.zenefitserver.domain.userpolicy.dao;

import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {

    Optional<UserPolicy> findByUser_userIdAndPolicy_Id(Long userId, Long policyId);

    List<UserPolicy> findAllByUser_userIdAndInterestFlag(Long userId, boolean interestFlag);
    List<UserPolicy> findAllByUser_userIdAndApplyFlag(Long userId, boolean ApplyFlag);

    List<UserPolicy> findAllByUser_userIdAndInterestFlagAndPolicy_ApplySttDateBetween(Long userId, boolean applyFlag, LocalDate searchSttDate, LocalDate searchEndDate);
    List<UserPolicy> findAllByUser_userIdAndInterestFlagAndPolicy_ApplyEndDateBetween(Long userId, boolean applyFlag, LocalDate searchSttDate, LocalDate searchEndDate);

    List<UserPolicy> findAllByPolicy_idAndInterestFlag(Long policyId,boolean interestFlag);

    @Query("SELECT count(up) FROM UserPolicy up WHERE up.user.userId = :userId AND up.interestFlag=true")
    int getInterestPolicyCount(@Param("userId") Long userId);

    @Query("SELECT count(up) FROM UserPolicy up WHERE up.user.userId = :userId AND up.applyFlag=true")
    int getApplyPolicyCount(@Param("userId") Long userId);
}
