package com.cmc.zenefitserver.domain.policy.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class PolicyLogoClassifier {

    private final PolicyRepository policyRepository;
    private final AmazonS3Client amazonS3Client;

    @Transactional
    public void saveLogo(){
        policyRepository.findAll().stream()
                .forEach(policy -> {
                    if(policy.getAreaCode()!=null && policy.getCityCode()!=null){
                        policy.updateLogo(getImageUrl(policy.getCityCode()));
                        return;
                    }
                    if(policy.getAreaCode()!=null){
                        policy.updateLogo(getImageUrl(policy.getAreaCode()));
                        return;
                    }
                    policy.updateLogo(getImageUrl(AreaCode.CENTRAL_GOVERNMENT));
                });
    }

    public String getImageUrl(Enum<?> codeEnum) {

        String bucketName = "zenefit-bucket";
        String folderName = null;
        String bucketImageUrl=null;

        if(codeEnum instanceof CityCode){
            folderName="city";
            bucketImageUrl = ((CityCode) codeEnum).getCode();
        }
        if(codeEnum instanceof AreaCode){
            folderName="area";
            bucketImageUrl = ((AreaCode) codeEnum).getCode();
        }

        String s3ObjectKey = folderName + "/" + bucketImageUrl + ".jpg"; // 이미지 파일의 객체 키

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, s3ObjectKey);

        java.net.URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString(); // 이미지 URL 반환
    }

}
