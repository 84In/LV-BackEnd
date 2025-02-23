package com.luanvan.mediaservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        // Lấy cấu hình từ System Properties hoặc có thể thay đổi lấy từ application.properties
        String cloudName = System.getProperty("cloudinary.cloudName");
        String apiKey = System.getProperty("cloudinary.apiKey");
        String apiSecret = System.getProperty("cloudinary.apiSecret");
        String uploadAssetsName = System.getProperty("cloudinary.uploadAssetsName");

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "upload_assets_name", uploadAssetsName
        ));
    }

    /**
     * Phương thức chung để upload file lên Cloudinary.
     * @param file File cần upload.
     * @param folder Đường dẫn thư mục trên Cloudinary (ví dụ: "users/123", "categories/456", "products/789").
     * @return URL của file được upload.
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto"  // tự động nhận dạng loại file
            );
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed", e);
        }
    }

    public String uploadFile(byte[] fileBytes, String folder) {
        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto"
            );
            Map uploadResult = cloudinary.uploader().upload(fileBytes, params);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed", e);
        }
    }

    // Các phương thức wrapper cho từng loại tài nguyên

    public String uploadAvatar(MultipartFile file, String userId) {
        return uploadFile(file, "users/" + userId);
    }

    public String uploadCategoryImage(MultipartFile file, String categoryId) {
        return uploadFile(file, "categories/" + categoryId);
    }

    public String uploadProductImage(List<MultipartFile> files, String productId) {
        return files.stream()
                .map(file -> uploadFile(file, "products/" + productId))
                .collect(Collectors.toList()).toString();
    }
}
