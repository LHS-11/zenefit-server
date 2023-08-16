package com.cmc.zenefitserver.global.config.fcm;

import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.cmc.zenefitserver.global.error.ErrorCode.NOTIFICATION_INTERNAL_SERVER_ERROR;

@Configuration
public class FCMConfig {

    @Bean
    FirebaseMessaging firebaseMessaging(){
        try {
            ClassPathResource resource = new ClassPathResource("firebase/zenefit-dbf19-firebase-adminsdk-iaour-b20189de47.json");

            InputStream refreshToken = resource.getInputStream();

            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

            if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
                for (FirebaseApp app : firebaseAppList) {
                    if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        firebaseApp = app;
                    }
                }
            } else {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(refreshToken))
                        .build();

                firebaseApp = FirebaseApp.initializeApp(options);
            }
            return FirebaseMessaging.getInstance(firebaseApp);
        } catch (IOException e) {
            throw new BusinessException(NOTIFICATION_INTERNAL_SERVER_ERROR);
        }
    }
}
