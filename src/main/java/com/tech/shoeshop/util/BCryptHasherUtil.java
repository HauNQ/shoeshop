package com.tech.shoeshop.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class BCryptHasherUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(14);

    private BCryptHasherUtil(){
        throw new UnsupportedOperationException("Utility class can not be instantiated");
    }

    public static String hash(String rawText){
        log.debug("Starting text hashing operation.");

        if(rawText == null || rawText.isBlank()){
            log.warn("Hashing failed: input text is null or blank");

            throw new IllegalArgumentException("Text can not be null or blank");
        }

        String hashedText = ENCODER.encode(rawText);

        log.info("Hashing completed successfully");

        return hashedText;
    }

    public static boolean matches(String rawText, String hashedText){
        log.debug("Starting hash verification operation.");

        if(rawText == null || rawText.isBlank() || hashedText.isBlank()){
            log.warn("Hash verification failed: rawText or hashedText is null.");
            return false;
        }

        boolean matched = ENCODER.matches(rawText, hashedText);

        log.info("Hash verification completed. Result={}", matched);

        return matched;
    }
}
