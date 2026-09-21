package vn.iotstar.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    record CloudinaryUploadResult(String url, String publicId) {}
    CloudinaryUploadResult upload(MultipartFile file);
    void delete(String publicId);
}
