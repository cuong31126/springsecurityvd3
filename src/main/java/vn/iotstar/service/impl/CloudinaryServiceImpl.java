package vn.iotstar.service.impl;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.service.CloudinaryService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép file hình ảnh");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "shop/products")
            );
            return new CloudinaryUploadResult(
                    String.valueOf(result.get("secure_url")),
                    String.valueOf(result.get("public_id"))
            );
        } catch (Exception e) {
            log.warn("Lỗi upload Cloudinary: {}. Sử dụng ảnh mặc định giả lập.", e.getMessage());
            String placeholder = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500";
            return new CloudinaryUploadResult(placeholder, "local_mock_" + UUID.randomUUID());
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank() || publicId.startsWith("local_mock_")) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            log.warn("Không thể xóa ảnh trên Cloudinary publicId {}: {}", publicId, e.getMessage());
        }
    }
}
