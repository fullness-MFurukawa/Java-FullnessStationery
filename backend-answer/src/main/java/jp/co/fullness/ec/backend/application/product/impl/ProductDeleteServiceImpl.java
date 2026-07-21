package jp.co.fullness.ec.backend.application.product.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.product.ProductDeleteService;
import jp.co.fullness.ec.backend.application.product.ProductSearchService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDeleteServiceImpl implements ProductDeleteService {

    private final ProductRepository productRepository;
    private final ProductSearchService productSearchService;

    @Override
    public Product findById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException("対象の商品が存在しません。"));
        if (product.isDeleted()) {
            throw new DomainException("対象の商品は既に削除されています。");
        }
        return product;
    }

    @Override
    public String generateImageUrl(String imageKey) {
        return productSearchService.generateImageUrl(imageKey);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException("対象の商品が存在しません。"));
        if (product.isDeleted()) {
            throw new DomainException("対象の商品は既に削除されています。");
        }
        productRepository.logicalDelete(id);
    }
}
