package com.cmc.zenefitserver.domain.policy.dao;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByBizId(String bizId);

    // jpql
    @Query("SELECT p FROM Policy p " +
            "WHERE p.supportPolicyType = :policyType " +
            "AND p.applyEndDate >= :currentDate " +
            "AND NOT EXISTS (SELECT up FROM UserPolicy up WHERE up.policy = p AND up.user.userId = :userId AND up.applyFlag=true) " +
            "ORDER BY p.applyEndDate ASC ")
    List<Policy> findMostImminentNonAppliedPolicy(@Param("userId") Long userId, @Param("policyType") SupportPolicyType policyType, @Param("currentDate") LocalDate currentDate, Pageable pageable);

    // native query
    @Query(value =
            "SELECT p.* FROM policy p " +
                    "WHERE p.support_policy_type = :policyType " +
                    "AND p.apply_end_date >= :currentDate " +
                    "AND NOT EXISTS (SELECT 1 FROM user_policy up WHERE up.policy_id = p.id AND up.user_id = :userId AND up.apply_flag = true) " +
                    "ORDER BY p.apply_end_date ASC " +
                    "LIMIT 1", nativeQuery = true)
    Policy findMostImminentNonAppliedPolicyNative(@Param("userId") Long userId, @Param("policyType") SupportPolicyType policyType, @Param("currentDate") LocalDate currentDate);


    List<Policy> findAllByApplyEndDate(LocalDate endDate);

    List<Policy> findAllByApplySttDate(LocalDate sttDate);

    List<Policy> findAllBySupportPolicyType(SupportPolicyType supportPolicyType);

    @EntityGraph(attributePaths = {"jobTypes", "educationTypes", "policySplzTypes", "supportPolicyTypes"})
    @Query(value = "SELECT p FROM Policy p " +
            "WHERE (p.areaCode = :areaCode AND p.cityCode = :cityCode) OR (p.areaCode = :areaCode AND p.cityCode IS NULL) OR (p.areaCode = :central) " +
            "AND :age between p.minAge and p.maxAge")
    List<Policy> findByAreaCodeAndCityCodeAndAge(@Param("areaCode") AreaCode areaCode, @Param("central") AreaCode central, @Param("cityCode") CityCode cityCode, @Param("age") int age);

}