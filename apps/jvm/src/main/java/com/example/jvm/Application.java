package com.example.jvm;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Application {

    public static void main(String[] args) {
        Flux.range(0, Integer.MAX_VALUE)
            .flatMap(index -> Mono.fromSupplier(() -> {
                try {
                    var key = generateKey(100);
                    return encrypt("input" + index, key);
                } catch (Exception ignored) {
                    return null;
                }
            }).subscribeOn(Schedulers.newBoundedElastic(10, 100, "encryption")))
            .buffer(10_000)
            .doOnNext(batch -> System.out.println(batch.size() + " done"))
            .blockLast();
    }

    private static String encrypt(String input, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
                InvalidAlgorithmParameterException, InvalidKeyException,
                BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
            .encodeToString(cipherText);
    }

    private static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

}
