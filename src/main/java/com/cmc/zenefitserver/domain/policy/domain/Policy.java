package com.cmc.zenefitserver.domain.policy.domain;


import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Entity
@Table(name = "policy", indexes = {
        @Index(name = "idx_policy_biz_id", columnList = "bizId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Policy implements Serializable {

    @Id
    @SequenceGenerator(
            name = "POLICY_SEQ_GENERATOR",
            sequenceName = "POLICY_SEQ",
            initialValue = 1, allocationSize = 100
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POLICY_SEQ_GENERATOR")
    private Long id;

    private String bizId; // 1. 정책 ID
    private String policyName; // 2. 정책명
    @Column(columnDefinition = "TEXT")
    private String policyIntroduction; // 3. 정책 소개
    private String operatingAgencyName; // 4. 운영기관명
    @Column(columnDefinition = "TEXT")
    private String applicationPeriodContent; // 5. 사업기간신청 내용
    private String organizationType; // 7. 기관 및 지자체 구분

    @Column(columnDefinition = "TEXT")
    private String supportContent; // 8. 지원 내용

    private String ageInfo; // 9. 참여요건 - 나이

    @Column(columnDefinition = "TEXT")
    private String employmentStatusContent; // 10. 취업상태내용 - empmSttsCn
    @Column(columnDefinition = "TEXT")
    private String specializedFieldContent; // 11. 특화분야내용 - splzRlmRqisCn

    @Column(columnDefinition = "TEXT")
    private String educationalRequirementContent; // 12. 학력요건내용 - accrRqisCn

    @Column(columnDefinition = "TEXT")
    private String residentialAndIncomeRequirementContent; // 13. 거주지 및 소득 조건 내용
    @Column(columnDefinition = "TEXT")
    private String additionalClauseContent; // 14. 추가단서사항 내용

    @Column(columnDefinition = "TEXT")
    private String eligibilityTargetContent; // 15. 참여제한대상 내용
    private String duplicatePolicyCode; // 중복불가정책 코드

    @Column(columnDefinition = "TEXT")
    private String applicationSiteAddress; // 16. 신청사이트 주소
    @Column(columnDefinition = "TEXT")
    private String referenceSiteUrlAddress; // 17. 참고사이트 Url 주소

    @Column(columnDefinition = "TEXT")
    private String applicationProcedureContent; // 18. 신청절차 내용

    @Column(columnDefinition = "TEXT")
    private String submissionDocumentContent; // 19. 제출서류 내용

    private int minAge; // 최소 나이

    private int maxAge; // 최대 나이

    @ElementCollection
    @CollectionTable(name = "policy_area_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<AreaCode> areaCodes = new HashSet<>(); // 지역 코드 - 시,도

    @ElementCollection
    @CollectionTable(name = "policy_city_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<CityCode> cityCodes = new HashSet<>(); // 지역 코드 - 구

    @ElementCollection
    @CollectionTable(name = "policy_job_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<JobType> jobTypes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "policy_education_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<EducationType> educationTypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private PolicyCode policyCode; // 정책 유형

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    private Set<UserPolicy> userPolicies = new HashSet<>();


    private String supportType; // 지원 유형 -> GPT?
    private int benefitAmount; // 수혜 금액

    public void updateAgeInfo(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public void updateJobTypes(Set<JobType> jobTypes) {
        this.jobTypes.addAll(jobTypes);
    }

    public void updateEducationTypes(Set<EducationType> educationTypes) {
        this.educationTypes.addAll(educationTypes);
    }

    public void updateAreaCode(AreaCode areaCode) {
        this.areaCodes.add(areaCode);
    }

    public void updateCityCode(CityCode cityCode) {
        this.cityCodes.add(cityCode);
    }

    @Builder
    public Policy(String bizId, String policyName, String policyIntroduction, String operatingAgencyName, String applicationPeriodContent, String organizationType, String supportContent, String ageInfo, String employmentStatusContent, String specializedFieldContent, String educationalRequirementContent, String residentialAndIncomeRequirementContent, String additionalClauseContent, String eligibilityTargetContent, String duplicatePolicyCode, String applicationSiteAddress, String referenceSiteUrlAddress, String applicationProcedureContent, String submissionDocumentContent, int minAge, int maxAge, Set<AreaCode> areaCodes, Set<CityCode> cityCodes, PolicyCode policyCode, String supportType, int benefitAmount) {
        this.bizId = bizId;
        this.policyName = policyName;
        this.policyIntroduction = policyIntroduction;
        this.operatingAgencyName = operatingAgencyName;
        this.applicationPeriodContent = applicationPeriodContent;
        this.organizationType = organizationType;
        this.supportContent = supportContent;
        this.ageInfo = ageInfo;
        this.employmentStatusContent = employmentStatusContent;
        this.specializedFieldContent = specializedFieldContent;
        this.educationalRequirementContent = educationalRequirementContent;
        this.residentialAndIncomeRequirementContent = residentialAndIncomeRequirementContent;
        this.additionalClauseContent = additionalClauseContent;
        this.eligibilityTargetContent = eligibilityTargetContent;
        this.duplicatePolicyCode = duplicatePolicyCode;
        this.applicationSiteAddress = applicationSiteAddress;
        this.referenceSiteUrlAddress = referenceSiteUrlAddress;
        this.applicationProcedureContent = applicationProcedureContent;
        this.submissionDocumentContent = submissionDocumentContent;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.areaCodes = areaCodes;
        this.cityCodes = cityCodes;
        this.policyCode = policyCode;
        this.supportType = supportType;
        this.benefitAmount = benefitAmount;
    }
}
