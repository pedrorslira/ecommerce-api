package com.pedroribeiro.ecommerce.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedroribeiro.ecommerce.dto.ProductDTO;
import com.pedroribeiro.ecommerce.dto.requests.ProductRequestDTO;
import com.pedroribeiro.ecommerce.model.Product;
import com.pedroribeiro.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO create(ProductRequestDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCreatedAt(date);
        product.setUpdatedAt(date);

        productRepository.save(product);
        return convertToDTO(product);
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ProductDTO findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToDTO(product);
    }

    public ProductDTO update(UUID id, ProductRequestDTO updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setStockQuantity(updatedProduct.getStockQuantity());
        product.setUpdatedAt(date);

        productRepository.save(product);
        return convertToDTO(product);
    }

    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

}
