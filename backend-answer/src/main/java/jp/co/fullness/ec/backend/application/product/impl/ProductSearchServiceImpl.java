package jp.co.fullness.ec.backend.application.product.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.fullness.ec.backend.application.product.ProductSearchService;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductSearchService} の実装。
 */
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageStorage imageStorage;  

    @Override
    public List<ProductCategory> findAllCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public List<Product> search(Integer categoryId, int page, int size) {
        int offset = (page - 1) * size;
        return productRepository.search(categoryId, size, offset);
    }

    @Override
    public long count(Integer categoryId) {
        return productRepository.count(categoryId);
    }

    @Override
    public String generateImageUrl(String key) {
        return imageStorage.generatePresignedUrl(key);
    }
}