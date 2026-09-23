package vn.iotstar.mapper;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;

public interface ProductMapper {
    ProductDTO toDTO(Product entity);
    Product toEntity(ProductDTO dto);
}
