package jp.co.fullness.ec.backend.application.product.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.product.ProductCreateService;
import jp.co.fullness.ec.backend.application.product.ProductRegisterCommand;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.factory.ProductCreateSource;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductCreateService} の実装。
 */
@Service
@RequiredArgsConstructor
public class ProductCreateServiceImpl implements ProductCreateService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Factory<Product, ProductCreateSource> productCreateFactory;
    private final ImageStorage imageStorage;

    @Override
    public List<ProductCategory> findAllCategories() {
        return productCategoryRepository.findAll();
    }

    /** ファイル名から拡張子(先頭ドット付き)を取り出す。無ければ空文字。 */
    private String extractExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot) : "";
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
    public void register(ProductRegisterCommand command) {
        ProductCategory category = productCategoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new DomainException("選択されたカテゴリが存在しません"));

        Product product = productCreateFactory.create(
                new ProductCreateSource(
                        command.getName(),
                        command.getPrice(),
                        command.getImageKey(),
                        category,
                        command.getStockQuantity()));

        productRepository.register(product);
    }
}