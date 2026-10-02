package org.example.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

    public class PasswordUtil {
        public static String generateSaltBase64() throws Exception {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        }

        public static String hashPassword(String password, String saltBase64) throws Exception {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100_000, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        }

        public static boolean verify(String password, String saltBase64, String expectedHashBase64) throws Exception {
            String h = hashPassword(password, saltBase64);
            return MessageDigest.isEqual(h.getBytes(), expectedHashBase64.getBytes());
        }
    }


