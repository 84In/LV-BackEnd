package com.luanvan.commonservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
public class ImageUtils {
    public static String encodeImageToBase64(byte[] image) {
        String result;
        result = Base64.getEncoder().encodeToString(image);
        return result;
    }

    public static byte[] decodeImageFromBase64(String imageBase64) {
        return Base64.getDecoder().decode(imageBase64);
    }
}
