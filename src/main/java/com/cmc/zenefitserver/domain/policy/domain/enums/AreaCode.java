package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.cmc.zenefitserver.domain.policy.domain.enums.CityCode.*;
import static com.cmc.zenefitserver.global.error.ErrorCode.NOT_FOUND_AREA_ENUM_VALUE;

@Getter
public enum AreaCode {
    SEOUL("003002001", "서울", Arrays.asList(
            JONGRO, JUNG, YONGSAN, SUNGDONG, GWANGJIN,
            DONGDAEMUN, JUNGNANG, SUNGBUK, GANGBUK, DOBONG,
            NOWON, EUNPYEONG, SEODAEMUN, MAPO, YANGCHEON,
            GANGSEO, GURO, GUMCHEON, YEONGDEUNGPO, DONGJAK,
            GWANAK, SECHO, GANGNAM, SONGPA, GANGDONG
    )),
    BUSAN("003002002", "부산", Arrays.asList(
            BUSAN_JUNG, BUSAN_SEO, BUSAN_DONG, BUSAN_YEONGDO,
            BUSAN_BUJIN, BUSAN_DONGRAE, BUSAN_NAM, BUSAN_BUK,
            BUSAN_HAEUNDAE, BUSAN_SAHAGU, BUSAN_GEUMJEONG, BUSAN_GANGSEO,
            BUSAN_YEONJE, BUSAN_SUYEONG, BUSAN_SASANG, BUSAN_GIJANG
    )),
    DAEGU("003002003", "대구", Arrays.asList(
            DAEGU_JUNG, DAEGU_DONG, DAEGU_SEO, DAEGU_NAM,
            DAEGU_BUK, DAEGU_SUSUNG, DAEGU_DALSEO, DAEGU_DALSEONG,
            DAEGU_GUNWI
    )),
    INCHEON("003002004", "인천", Arrays.asList(
            INCHEON_JUNG, INCHEON_DONG, INCHEON_MICHEUHOL, INCHEON_YEONSU,
            INCHEON_NAMDONG, INCHEON_BUPYEONG, INCHEON_GYEGWANG, INCHEON_SEO,
            INCHEON_GANGHWA, INCHEON_ONGJIN
    )),
    GWANGJU("003002005", "광주", Arrays.asList(
            GWANGJU_DONG, GWANGJU_SEO, GWANGJU_NAM, GWANGJU_BUK,
            GWANGJU_GWANGSAN
    )),
    DAEJEON("003002006", "대전", Arrays.asList(
            DAEJEON_DONG, DAEJEON_JUNG, DAEJEON_SEO, DAEJEON_YUSEONG,
            DAEJEON_DAEDEOK
    )),
    ULSAN("003002007", "울산", Arrays.asList(
            ULSAN_JUNG, ULSAN_NAM, ULSAN_DONG, ULSAN_BUK,
            ULSAN_ULJU
    )),
    GYEONGGI("003002008", "경기", Arrays.asList(
            GYEONGGI_SUWON, GYEONGGI_SEONGNAM, GYEONGGI_UIJEONGBU, GYEONGGI_ANYANG,
            GYEONGGI_BUCHEON, GYEONGGI_GWANGMYEONG, GYEONGGI_PYONGTAEK, GYEONGGI_DONGDUCHEON,
            GYEONGGI_ANSAN, GYEONGGI_GOYANG, GYEONGGI_GWACHEON, GYEONGGI_GURI,
            GYEONGGI_NAMYANGJU, GYEONGGI_OSAN, GYEONGGI_SIHEUNG, GYEONGGI_GUNPO,
            GYEONGGI_UIWANG, GYEONGGI_HANAM, GYEONGGI_YONGIN, GYEONGGI_PAJU,
            GYEONGGI_ICHEON, GYEONGGI_ANSEONG, GYEONGGI_GIMPO, GYEONGGI_HWASEONG,
            GYEONGGI_GWANGJU, GYEONGGI_YANGJU, GYEONGGI_POCHUN, GYEONGGI_YEOJU,
            GYEONGGI_YEONCHEON, GYEONGGI_GAPYEONG, GYEONGGI_YANGPYEONG
    )),
    GANGWON("003002009", "강원", Arrays.asList(
            GANGWON_CHUNCHEON, GANGWON_WONJU, GANGWON_GANGNEUNG, GANGWON_DONGHAE,
            GANGWON_TAEBACK, GANGWON_SOCKCHO, GANGWON_SAMCHEOK, GANGWON_HONGCHEON,
            GANGWON_HOENGSEONG, GANGWON_YEONGWOL, GANGWON_PYOUNGCHANG, GANGWON_JEONGSEON,
            GANGWON_CHEORWON, GANGWON_HWACHEON, GANGWON_YANGGU, GANGWON_INJE,
            GANGWON_GOSEONG, GANGWON_YANGYANG
    )),
    CHUNGBUK("003002010", "충북", Arrays.asList(
            CHUNGBUK_CHEONGJU, CHUNGBUK_CHUNGJU, CHUNGBUK_JECHEON, CHUNGBUK_BOEUN,
            CHUNGBUK_OKCHEON, CHUNGBUK_YEONGDONG, CHUNGBUK_JEUNGPYEONG, CHUNGBUK_JINCHEON,
            CHUNGBUK_GOESAN, CHUNGBUK_EUMSEONG, CHUNGBUK_DANYANG
    )),
    CHUNGNAM("003002011", "충남", Arrays.asList(
            CHUNGNAM_CHEONAN, CHUNGNAM_GONGJU, CHUNGNAM_BORYEONG, CHUNGNAM_ASAN,
            CHUNGNAM_SEOSAN, CHUNGNAM_NONSAN, CHUNGNAM_GYERYONG, CHUNGNAM_DANGJIN,
            CHUNGNAM_GEUMSAN, CHUNGNAM_BUYEO, CHUNGNAM_SEOCHEON, CHUNGNAM_CHEONGYANG,
            CHUNGNAM_HONGSEONG, CHUNGNAM_YESAN, CHUNGNAM_TAEAN
    )),
    JEONBUK("003002012", "전북", Arrays.asList(
            JEONBUK_JEONJU, JEONBUK_GUNSAN, JEONBUK_IKSAN, JEONBUK_JEONGEUP,
            JEONBUK_NAMWON, JEONBUK_GIMJE, JEONBUK_WANJU, JEONBUK_JINAN,
            JEONBUK_MUJU, JEONBUK_JANGSU, JEONBUK_IMSIL, JEONBUK_SUNCHANG,
            JEONBUK_GOCHANG, JEONBUK_BUAN
    )),
    JEONNAM("003002013", "전남", Arrays.asList(
            JEONNAM_MOKPO, JEONNAM_YEOSU, JEONNAM_SUNCHEON, JEONNAM_NAJU,
            JEONNAM_GWANGYANG, JEONNAM_DAMYANG, JEONNAM_GOKSEONG, JEONNAM_GURYE,
            JEONNAM_GOHEUNG, JEONNAM_BOSEONG, JEONNAM_HWASUN, JEONNAM_JANGHEUNG,
            JEONNAM_GANGJIN, JEONNAM_HAENAM, JEONNAM_YEONGAM, JEONNAM_MUAN,
            JEONNAM_HAMPYEONG, JEONNAM_YEONGGWANG, JEONNAM_JANGSEONG, JEONNAM_WANDO,
            JEONNAM_JINDO, JEONNAM_SINAN
    )),
    GYEONGBUK("003002014", "경북", Arrays.asList(
            GYEONGBUK_POHANG, GYEONGBUK_GYEONGJU, GYEONGBUK_GIMCHEON, GYEONGBUK_ANDONG,
            GYEONGBUK_GUMI, GYEONGBUK_YEONGJU, GYEONGBUK_YEONGCHUN, GYEONGBUK_SANGJU,
            GYEONGBUK_MUNGYEONG, GYEONGBUK_GYEONGSAN, GYEONGBUK_UISEONG, GYEONGBUK_CHEONGSONG,
            GYEONGBUK_YEONGYANG, GYEONGBUK_YEONGDEOK, GYEONGBUK_CHEONGDO, GYEONGBUK_GORYEONG,
            GYEONGBUK_SEONGJU, GYEONGBUK_CHILGOK, GYEONGBUK_YECHEON, GYEONGBUK_BONGHWA,
            GYEONGBUK_ULJIN, GYEONGBUK_ULLEUNG
    )),
    GYEONGNAM("003002015", "경남", Arrays.asList(
            GYEONGNAM_CHANGWON, GYEONGNAM_JINJU, GYEONGNAM_TONGYEONG, GYEONGNAM_SACHEON,
            GYEONGNAM_GIMHAE, GYEONGNAM_MIRYANG, GYEONGNAM_GEOJE, GYEONGNAM_YANGSAN,
            GYEONGNAM_YEORYEONG, GYEONGNAM_HAMAN, GYEONGNAM_CHANGNYEONG, GYEONGNAM_GOSEONG,
            GYEONGNAM_NAMHAE, GYEONGNAM_HADONG, GYEONGNAM_SANSCHEONG, GYEONGNAM_HAMYANG,
            GYEONGNAM_GEOCHANG, GYEONGNAM_HAPCHEON
    )),
    JEJU("003002016", "제주", Arrays.asList(
            JEJU_JEJU, JEJU_SEOGWIPO
    )),
    SEJONG("003002017", "세종", Arrays.asList(
            SEJONG_SEONG
    )),
    CENTRAL_GOVERNMENT("003002000", "중앙부처", null);

    private final String code;
    private final String name;


    private final List<CityCode> cities;

    AreaCode(String code, String name, List<CityCode> cities) {
        this.code = code;
        this.name = name;
        this.cities = cities;
    }

    public List<CityCode> getCities() {
        return cities;
    }

    public static AreaCode findAreaCode(String code) {
        return Arrays.stream(AreaCode.values())
                .filter(a -> a.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static List<String> findCityCodes(String name) {
        return Arrays.stream(AreaCode.values())
                .filter(code -> code.getName().equals(name))
                .findFirst()
                .map(code -> code.getCities().stream()
                        .map(cityCode -> cityCode.getName())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new BusinessException(NOT_FOUND_AREA_ENUM_VALUE));
    }

    public static String findName(String areaCode){
        return Arrays.stream(AreaCode.values())
                .filter(code -> code.name().equals(areaCode))
                .findFirst()
                .map(code -> code.getName())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_AREA_ENUM_VALUE));
    }

}