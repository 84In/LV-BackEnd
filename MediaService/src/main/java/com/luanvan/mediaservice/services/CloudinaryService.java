package com.luanvan.mediaservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloudName}")
    private String cloudName;

    @Value("${cloudinary.apiKey}")
    private String apiKey;

    @Value("${cloudinary.apiSecret}")
    private String apiSecret;

    @Value("${cloudinary.uploadAssetsName}")
    private String uploadAssetsName;

    public String uploadAvatar(byte[] avatar, String userId) {
        Cloudinary cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "upload_assets_name", uploadAssetsName
                )
        );

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder","users/"+ userId,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(avatar, params);
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Upload avatar failed", e);
        }
    }

}
