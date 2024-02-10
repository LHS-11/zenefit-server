package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PolicyEduClassifier {

    public Set<EducationType> mapToEducationTypeFromEduContent(String eduContent) {
        Set<EducationType> eduContentEducationTypes = new HashSet<>();

        if (eduContent.contains("고졸 미만") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")) {
            eduContentEducationTypes.add(EducationType.BELOW_HIGH_SCHOOL);
        }
        if (eduContent.contains("고교 재학") || eduContent.contains("고교재학")
                || eduContent.contains("고등학교 3학년 재학생으로 정규학기 졸업일(졸업예정일)에 졸업예정자 또는 졸업자(대한민국 국적자)")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("국내 고교 2, 3학년 재학(예정)자")) {
            eduContentEducationTypes.add(EducationType.HIGH_SCHOOL_STUDENT);
        }
        if (eduContent.contains("고졸 예정")
                || eduContent.contains("고등학교 3학년 재학생으로 정규학기 졸업일(졸업예정일)에 졸업예정자 또는 졸업자(대한민국 국적자)")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("일반계 고등학교에 재학 중이며 대학 진학을 하지 않고 취업을 희망하는 3학년 학생") || eduContent.contains("미취업 국적 청년해기사(해사고, 해양대 졸업예정자 등 포함)")
                || eduContent.contains("중·고교, 대학, 대학원 졸업(예정)") || eduContent.contains("- 졸업자 또는 졸업예정자")) {
            eduContentEducationTypes.add(EducationType.HIGH_SCHOOL_GRADUATION_EXPECTED);
        } else if (eduContent.contains("고교 졸업") || eduContent.contains("고졸")
                || eduContent.contains("고등학교 3학년 재학생으로 정규학기 졸업일(졸업예정일)에 졸업예정자 또는 졸업자(대한민국 국적자)")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)") || eduContent.contains("고교졸업")
                || eduContent.contains("일반계 고등학교에 재학 중이며 대학 진학을 하지 않고 취업을 희망하는 3학년 학생")
                || eduContent.contains("고등학교 졸업 또는 검정고시 등 이와 동등 이상의 학력이 있다고 인정되는 자")
                || eduContent.contains("고등학교 졸업자 혹은 대학 최종학년 재학생 및 미취업자") || eduContent.contains("고등학교 졸업이상 학력자")
                || eduContent.contains("채용일 기준 고등학교 또는 대학교 재학 중인자는 지원 제외") || eduContent.contains("미취업 국적 청년해기사(해사고, 해양대 졸업예정자 등 포함)")
                || eduContent.contains("최종학력 졸업(중퇴·제적)자 (학력무관)") || eduContent.contains("고등학교, 대학(원) 졸업자(예정자)")
                || eduContent.contains("중·고교, 대학, 대학원 졸업(예정)") || eduContent.contains("- 졸업자 또는 졸업예정자")) {
            eduContentEducationTypes.add(EducationType.HIGH_SCHOOL_GRADUATE);

        } else if (eduContent.contains("대학 재학") || eduContent.contains("대학생") || eduContent.contains("대학재학") || eduContent.contains("신입생") || eduContent.contains("학부생")
                || eduContent.contains("해당 대학에 입학 예정이거나 재학 중인 대학(원)생(외국인 포함)")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("고등학교 졸업 또는 검정고시 등 이와 동등 이상의 학력이 있다고 인정되는 자") || eduContent.contains("대학(원)생")
                || eduContent.contains("고등학교 졸업자 혹은 대학 최종학년 재학생 및 미취업자") || eduContent.contains("고등학교 졸업이상 학력자")
                || eduContent.contains("대학교 재학생") || eduContent.contains("대학 또는 대학원 재학,휴학,졸업예정자")) {
            eduContentEducationTypes.add(EducationType.COLLEGE_STUDENT);
        } else if (eduContent.contains("대졸 예정") || eduContent.contains("대학(원)졸업예정자") || eduContent.contains("대학 졸업(예정)") || eduContent.contains("고등·대학교 또는 대학원 재학생(휴학생)지원불가, 단, 대학교 졸업유예(예정)자, 방송통신대 및 사이버대학교 재학생은 지원 가능")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("고등학교 졸업 또는 검정고시 등 이와 동등 이상의 학력이 있다고 인정되는 자")
                || eduContent.contains("고등학교 졸업자 혹은 대학 최종학년 재학생 및 미취업자") || eduContent.contains("대학교 졸업예정자")
                || eduContent.contains("고등학교 졸업이상 학력자") || eduContent.contains("미취업 국적 청년해기사(해사고, 해양대 졸업예정자 등 포함)")
                || eduContent.contains("고등학교, 대학(원) 졸업자(예정자)") || eduContent.contains("대학 또는 대학원 재학,휴학,졸업예정자")
                || eduContent.contains("중·고교, 대학, 대학원 졸업(예정)") || eduContent.contains("- 졸업자 또는 졸업예정자")) {
            eduContentEducationTypes.add(EducationType.COLLEGE_GRADUATION_EXPECTED);
        } else if (eduContent.contains("대학 졸업") || eduContent.contains("대학졸업") || eduContent.contains("학사 이상")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("대졸") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("고등학교 및 대학교 재학생 참여불가")
                || eduContent.contains("고등학교 졸업 또는 검정고시 등 이와 동등 이상의 학력이 있다고 인정되는 자")
                || eduContent.contains("고등학교 졸업자 혹은 대학 최종학년 재학생 및 미취업자")
                || eduContent.contains("학부 재학생 지원불가") || eduContent.contains("고등학교 졸업이상 학력자") || eduContent.contains("채용일 기준 고등학교 또는 대학교 재학 중인자는 지원 제외")
                || eduContent.contains("미취업 국적 청년해기사(해사고, 해양대 졸업예정자 등 포함)") || eduContent.contains("최종학력 졸업(중퇴·제적)자 (학력무관)")
                || eduContent.contains("고등학교, 대학(원) 졸업자(예정자)") || eduContent.contains("대학 또는 대학원 재학,휴학,졸업예정자")
                || eduContent.contains("중·고교, 대학, 대학원 졸업(예정)") || eduContent.contains("- 졸업자 또는 졸업예정자")) {
            eduContentEducationTypes.add(EducationType.COLLEGE_GRADUATE);
        } else if (eduContent.contains("석·박사") || eduContent.contains("석박사") || eduContent.contains("박사")
                || eduContent.contains("석사") || eduContent.contains("대학원생") || eduContent.contains("학사 이상")
                || eduContent.contains("고등학생, 대학(원)생, 졸업생") || eduContent.contains("만 14세 이상의 지원 대상자(학생)")
                || eduContent.contains("고등학교 및 대학교 재학생 참여불가") || eduContent.contains("고등학교 졸업 또는 검정고시 등 이와 동등 이상의 학력이 있다고 인정되는 자")
                || eduContent.contains("대학(원)생") || eduContent.contains("학부 재학생 지원불가") || eduContent.contains("고등학교 졸업이상 학력자")
                || eduContent.contains("채용일 기준 고등학교 또는 대학교 재학 중인자는 지원 제외") || eduContent.contains("고등학교, 대학(원) 졸업자(예정자)")
                || eduContent.contains("대학 또는 대학원 재학,휴학,졸업예정자") || eduContent.contains("중·고교, 대학, 대학원 졸업(예정)")
                || eduContent.contains("- 졸업자 또는 졸업예정자")) {
            eduContentEducationTypes.add(EducationType.POSTGRADUATE);
        } else if (eduContent.contains("제한없음") || eduContent.contains("기타") || eduContent.equals("") || eduContent.equals("null")
                || eduContent.equals("-") || eduContent.contains("한없음") || eduContent.contains("제한 없음")) {
            eduContentEducationTypes.add(EducationType.UNLIMITED);
        }
        return eduContentEducationTypes;
    }
}
