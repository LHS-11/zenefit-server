package com.cmc.zenefitserver.global.infra.notification.domain;

public enum NotificationImage {

    STT_DATE("https://giftyyy.shop/image/alarm/STT_DATE.png"),
    END_DATE("https://giftyyy.shop/image/alarm/END_DATE.png");

     final String imageUrl;

    NotificationImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
