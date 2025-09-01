package com.madebawojo.nysc.ppa.clearance.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class OpaqueTokenUtil {
    private final SecureRandom random = new SecureRandom();
    private final MessageDigest sha256;

    public OpaqueTokenUtil() {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        }
        catch (Exception e) { throw new IllegalStateException(e); }
    }

    public String generateRawToken() {
        byte[] bytes = new byte[32]; // 256-bit
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String raw) {
        return HexFormat.of().formatHex(sha256.digest(raw.getBytes(StandardCharsets.UTF_8)));
    }

    public String newFamilyId() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

