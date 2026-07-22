package jp.co.fullness.ec.frontend.application.product.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.fullness.ec.frontend.application.product.ProductSearchService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;
import jp.co.fullness.ec.frontend.domain.storage.ImageStorage;

import lombok.RequiredArgsConstructor;

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
    public List<Product> searchByCategory(Integer categoryId) {
        return productRepository.searchByCategory(categoryId);
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DomainException("指定された商品は存在しません"));
    }

    @Override
    public String generateImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return imageStorage.generatePresignedUrl(imageKey);
    }
}
