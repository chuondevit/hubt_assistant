package com.hubt.assistant.security.token;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final SecureRandom secureRandom =
            new SecureRandom();

    public String generateSixDigitOtp() {

        int number =
                secureRandom.nextInt(1_000_000);

        return String.format(
                "%06d",
                number
        );
    }
}