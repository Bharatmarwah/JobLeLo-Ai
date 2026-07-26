package in.joblelo.JobAgentBackend.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class encryptionKeyUtil {
    public static void main(String[] args) throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator =
                    KeyGenerator.getInstance("AES");

            keyGenerator.init(256);

            SecretKey secretKey =
                    keyGenerator.generateKey();

            String key = Base64.getEncoder()
                    .encodeToString(secretKey.getEncoded());

            System.out.println(key);
        }
    }
