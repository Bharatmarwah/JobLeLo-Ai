package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Value("${gmail.encryption.key}")
    private String base64Key;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    private void init() {
        if (base64Key == null || base64Key.isBlank()) {
            throw new ApiException(
                    "gmail.encryption.key property not found or is blank (set GMAIL_ENCRYPTION_KEY env variable or property)", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(base64Key);
        } catch(Exception e) {
            throw new ApiException("Failed to decode encryption key from base64", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (keyBytes.length != 32) { // 256 bits
            throw new ApiException("Encryption key must be exactly 32 bytes (256 bits). Current length: " + keyBytes.length, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
    }

    private SecretKeySpec getSecretKey() {
        if (secretKeySpec == null) {
            throw new ApiException("Encryption key not initialized", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return secretKeySpec;
    }

    public String encrypt(String plainText) {

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            byte[] encryptedBytes =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] ivAndCiphertext = new byte[GCM_IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, ivAndCiphertext, GCM_IV_LENGTH, encryptedBytes.length);

            return Base64.getEncoder()
                    .encodeToString(ivAndCiphertext);

        } catch (Exception e) {
            throw new ApiException(
                    "Failed to encrypt data", e, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public String decrypt(String encryptedText) {

        try {
            byte[] decoded = Base64.getDecoder()
                    .decode(encryptedText);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, GCM_IV_LENGTH);

            byte[] ciphertext = new byte[decoded.length - GCM_IV_LENGTH];
            System.arraycopy(decoded, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            byte[] decryptedBytes = cipher.doFinal(ciphertext);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new ApiException(
                    "Failed to decrypt data", e, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}