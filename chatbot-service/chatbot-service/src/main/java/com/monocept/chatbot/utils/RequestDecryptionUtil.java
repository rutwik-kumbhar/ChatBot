package com.monocept.chatbot.utils;

import com.monocept.chatbot.exceptions.RequestDecryptionException;
import com.monocept.chatbot.exceptions.RequestEncryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class RequestDecryptionUtil {

    @Value("${payload.encryption.key}")
    private String payloadEncryptionKey;

    @Value("${payload.encryption.iv}")
    private String payloadEncryptionIv;

    @Value("${payload.encryption.type}")
    private String encryptionType;

    @Value("${payload.decryption.type}")
    private String decryptionType;


    public String encrypt(String data) {
        String key = payloadEncryptionKey;
        String iv = payloadEncryptionIv;
        try {
            Cipher cipher = Cipher.getInstance(encryptionType);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.info("encrypt : Exception occurred while encrypting : {}",e.getMessage());
            throw new RequestEncryptionException("Exception occurred while encrypting : " +  e.getMessage());
        }
    }

    public String decrypt(String encryptedData) {
        String key =  payloadEncryptionKey;
        String iv =  payloadEncryptionIv;
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            Cipher cipher = Cipher.getInstance(decryptionType);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] original = cipher.doFinal(encryptedBytes);
            return new String(original).trim();
        } catch (Exception e) {
            throw  new RequestDecryptionException("Exception occurred while decrypting : " + e.getMessage());
        }
    }

}
