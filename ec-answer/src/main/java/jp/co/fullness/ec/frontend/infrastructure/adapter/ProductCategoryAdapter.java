package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductCategoryEntity;

/**
 * ProductCategory ⇄ ProductCategoryEntity の変換アダプタ。
 */
@Component
public class ProductCategoryAdapter implements Adapter<ProductCategory, ProductCategoryEntity> {

    @Override
    public ProductCategory toDomain(ProductCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProductCategory.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public ProductCategoryEntity fromDomain(ProductCategory domain) {
        if (domain == null) {
            return null;
        }
        return ProductCategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
