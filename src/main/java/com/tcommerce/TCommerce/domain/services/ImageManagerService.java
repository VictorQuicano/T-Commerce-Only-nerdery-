package com.tcommerce.TCommerce.domain.services;

import java.security.SecureRandom;
import java.util.Random;

public class ImageManagerService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int length = 50;
    private static final Random random = new SecureRandom();

    // Delete after
    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String uploadImage() {
        return generateRandomString();
    }

    public static boolean deleteImage() {
        return true;
    }
    public static String updateImage(String urlToUpdate){
        return generateRandomString();
    }
}
