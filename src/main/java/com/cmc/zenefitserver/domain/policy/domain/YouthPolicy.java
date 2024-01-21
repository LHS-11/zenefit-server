package com.cmc.zenefitserver.domain.policy.domain;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class YouthPolicy {

    @XmlElement(name = "bizId")
    private String bizId; // 1. 정책 ID

    @XmlElement(name = "polyBizSjnm")
    private String polyBizSjnm; // 2. 정책명

    @XmlElement(name = "polyItcnCn")
    private String polyItcnCn; // 3. 정책 소개

    @XmlElement(name = "cnsgNmor")
    private String cnsgNmor; // 4. 운영기관명

    @XmlElement(name = "rqutPrdCn")
    private String rqutPrdCn; // 5. 사업신청기간내용명

    @XmlElement(name = "polyBizTy")
    private String polyBizTy; // 7. 기관 및 지자체 구분

    @XmlElement(name = "sporCn")
    private String sporCn; // 8. 지원내용

    @XmlElement(name = "ageInfo")
    private String ageInfo; // 9. 참여요건 - 나이

    @XmlElement(name = "empmSttsCn")
    private String empmSttsCn; // 10. 취업상태내용

    @XmlElement(name = "splzRlmRqisCn")
    private String splzRlmRqisCn; // 11. 특화분야내용

    @XmlElement(name = "accrRqisCn")
    private String accrRqisCn; // 12. 학력요건내용

    @XmlElement(name = "prcpCn")
    private String prcpCn; // 13. 거주지 및 소득조건 내용

    @XmlElement(name = "aditRscn")
    private String aditRscn; // 14. 추가단서사항 내용

    @XmlElement(name = "prcpLmttTrgtCn")
    private String prcpLmttTrgtCn; // 15. 참여제한대상 내용

    @XmlElement(name = "rqutUrla")
    private String rqutUrla; // 16. 신청사이트 주소

    @XmlElement(name = "rfcSiteUrla1")
    private String rfcSiteUrla1; // 17. 참고사이트URL주소1

    @XmlElement(name = "rqutProcCn")
    private String rqutProcCn; // 18. 신청절차내용

    @XmlElement(name = "pstnPaprCn")
    private String pstnPaprCn; // 19. 제출서류내용

    @XmlElement(name = "polyRlmCd") // 20. 정책분야코드
    private String polyRlmCd;

    @XmlElement(name = "error")
    private String error;

    private AreaCode areaCode;

    private CityCode cityCode;

    public void updateRegion(AreaCode areaCode, CityCode cityCode){
        if(cityCode != null){
            updateCityCode(cityCode);
        }
        updateAreaCode(areaCode);
    }

    public void updateAreaCode(AreaCode areaCode){
        this.areaCode = areaCode;
    }
    public void updateCityCode(CityCode cityCode){
        this.cityCode = cityCode;
    }
}
