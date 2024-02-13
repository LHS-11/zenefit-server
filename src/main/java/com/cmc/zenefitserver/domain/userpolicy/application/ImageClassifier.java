package com.cmc.zenefitserver.domain.userpolicy.application;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.user.domain.Character;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageClassifier {

    @Value("${s3.url}")
    private String s3Url;

    public String getCharacterImage(Gender gender, Character character){
        return s3Url + gender.getUrl() + "-" + character.name().toLowerCase() + ".png";
    }

    public String getLogo(Policy policy) {
        if (policy.getAreaCode() != null && policy.getCityCode() != null) {
            return getLogoUrl(policy.getCityCode());
        }
        if (policy.getAreaCode() != null) {
            return getLogoUrl(policy.getAreaCode());
        }
        return null;
    }

    private String getLogoUrl(Enum<?> codeEnum) {

        String folderName = null;
        String bucketImageUrl = null;

        if (codeEnum instanceof CityCode) {
            folderName = "city";
            bucketImageUrl = ((CityCode) codeEnum).getCode();
        }
        if (codeEnum instanceof AreaCode) {
            folderName = "area";
            bucketImageUrl = ((AreaCode) codeEnum).getCode();
        }

        String s3ObjectUrl = folderName + "/" + bucketImageUrl + ".jpg"; // 이미지 파일의 객체 키

        return s3Url + s3ObjectUrl; // 이미지 URL 반환
    }

//    private String getLogoImageUrl(Enum<?> codeEnum) {
//
//        String bucketName = "zenefit-bucket";
//        String folderName = null;
//        String bucketImageUrl=null;
//
//        if(codeEnum instanceof CityCode){
//            folderName="city";
//            bucketImageUrl = ((CityCode) codeEnum).getCode();
//        }
//        if(codeEnum instanceof AreaCode){
//            folderName="area";
//            bucketImageUrl = ((AreaCode) codeEnum).getCode();
//        }
//
//        String s3ObjectKey = folderName + "/" + bucketImageUrl + ".jpg"; // 이미지 파일의 객체 키
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(bucketName, s3ObjectKey);
//
//        java.net.URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
//        return url.toString(); // 이미지 URL 반환
//    }

}
