package com.luanvan.mediaservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    public String uploadAvatar(MultipartFile avatar, String userId) {

        String cloudName = System.getProperty("cloudinary.cloudName");
        String apiKey = System.getProperty("cloudinary.apiKey");
        String apiSecret = System.getProperty("cloudinary.apiSecret");
        String uploadAssetsName = System.getProperty("cloudinary.uploadAssetsName");

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

            Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), params);
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Upload avatar failed", e);
        }
    }

}
