package jp.co.fullness.ec.backend.application.product.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.product.ProductUpdateCommand;
import jp.co.fullness.ec.backend.application.product.ProductUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductUpdateService} の実装。
 */
@Service
@RequiredArgsConstructor
public class ProductUpdateServiceImpl implements ProductUpdateService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageStorage imageStorage;

    @Override
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public List<ProductCategory> findAllCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public String uploadImage(byte[] content, String contentType, String originalFileName) {
        String key = "products/" + UUID.randomUUID() + extractExtension(originalFileName);
        imageStorage.upload(key, content, contentType);
        return key;
    }

    @Override
    public String generateImageUrl(String key) {
        return imageStorage.generatePresignedUrl(key);
    }

    @Override
    @Transactional
    public void update(ProductUpdateCommand command) {
        // 既存集約を取得
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new DomainException("対象の商品が存在しません"));
        ProductCategory category = productCategoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new DomainException("選択されたカテゴリが存在しません"));

        // 変更を反映
        product.setName(command.getName());
        product.setPrice(command.getPrice());
        product.setCategory(category);
        product.setImageUrl(command.getImageKey());
        if (product.getStock() != null) {
            product.getStock().setQuantity(command.getStockQuantity());
        } else {
            product.setStock(ProductStock.builder().quantity(command.getStockQuantity()).build());
        }

        // 保存
        productRepository.update(product);
    }

    private String extractExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot) : "";
    }
}
