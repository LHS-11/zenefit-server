package com.cmc.zenefitserver.domain.policy.domain;


import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    private Set<JobType> jobTypes = new HashSet<>(); // 직업 유형

    @ElementCollection
    @CollectionTable(name = "policy_education_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<EducationType> educationTypes = new HashSet<>(); // 학력 유형

    @ElementCollection
    @CollectionTable(name = "policy_splz_code_list", joinColumns = @JoinColumn(name = "biz_id", referencedColumnName = "bizId"))
    @Enumerated(EnumType.STRING)
    private Set<PolicySplzType> policySplzTypes = new HashSet<>(); // 특화 분야 유형

    @Enumerated(EnumType.STRING)
    private PolicyCode policyCode; // 정책 유형

    @Enumerated(EnumType.STRING)
    private SupportPolicyType supportPolicyType; // 지원 정책 유형

    private String agency; // 기관

    private String agencyLogo; // 임시 기관 로고

    private String policyApplyDenialReason; // 신청 불가 사유

    private String applyStatus; // 신청 가능 상태

    private LocalDate sttDate; // 신청 시작일

    private LocalDate endDate; // 신청 종료일


    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    private Set<UserPolicy> userPolicies = new HashSet<>();

    private int benefit; // 수혜 금액

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

    public void updateSplzTypes(Set<PolicySplzType> policySplzTypes) {
        this.policySplzTypes.addAll(policySplzTypes);
    }

    public void updateAreaCode(AreaCode areaCode) {
        this.areaCodes.add(areaCode);
    }

    public void updateCityCode(CityCode cityCode) {
        this.cityCodes.add(cityCode);
    }

    @Builder
    public Policy(String bizId, String policyName, String policyIntroduction, String operatingAgencyName, String applicationPeriodContent, String organizationType, String supportContent, String ageInfo, String employmentStatusContent, String specializedFieldContent, String educationalRequirementContent, String residentialAndIncomeRequirementContent, String additionalClauseContent, String eligibilityTargetContent, String duplicatePolicyCode, String applicationSiteAddress, String referenceSiteUrlAddress, String applicationProcedureContent, String submissionDocumentContent, int minAge, int maxAge, Set<AreaCode> areaCodes, Set<CityCode> cityCodes, Set<JobType> jobTypes, Set<EducationType> educationTypes, Set<PolicySplzType> policySplzTypes, PolicyCode policyCode, SupportPolicyType supportPolicyType, String agency, String agencyLogo, String policyApplyDenialReason, String applyStatus, LocalDate sttDate, LocalDate endDate, Set<UserPolicy> userPolicies, int benefit) {
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
        this.supportPolicyType = supportPolicyType;
        this.agency = agency;
        this.agencyLogo = agencyLogo;
        this.policyApplyDenialReason = policyApplyDenialReason;
        this.applyStatus = applyStatus;
        this.sttDate = sttDate;
        this.endDate = endDate;
        this.userPolicies = userPolicies;
        this.benefit = benefit;
    }
}
