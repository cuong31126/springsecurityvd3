package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.CloudinaryService;
import vn.iotstar.service.ProductService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper mapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = (keyword == null || keyword.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.search(keyword.trim(), pageable);

        return products.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại với ID: " + id));
        return mapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO create(ProductDTO dto, MultipartFile image) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth != null ? auth.getName() : "admin";

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("Không tìm thấy User hiện tại")));

        Product product = mapper.toEntity(dto);
        product.setUser(currentUser);
        product.setCreatedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            CloudinaryService.CloudinaryUploadResult result = cloudinaryService.upload(image);
            product.setImageUrl(result.url() + "|" + result.publicId());
        }

        return mapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại với ID: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        if (image != null && !image.isEmpty()) {
            String old = product.getImageUrl();
            if (old != null && old.contains("|")) {
                cloudinaryService.delete(old.substring(old.indexOf('|') + 1));
            }
            CloudinaryService.CloudinaryUploadResult r = cloudinaryService.upload(image);
            product.setImageUrl(r.url() + "|" + r.publicId());
        }

        return mapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại với ID: " + id));

        String image = product.getImageUrl();
        if (image != null && image.contains("|")) {
            cloudinaryService.delete(image.substring(image.indexOf('|') + 1));
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUser(Long userId) {
        return productRepository.countByUserId(userId);
    }
}
