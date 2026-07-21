package jp.co.fullness.ec.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.adapter.Adapter;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;

/**
 * Product ⇄ ProductEntity の変換アダプタ。
 * <p>toDomain ではカテゴリ・在庫は設定しない(Entity は id しか持たないため)。
 * 完全な集約の組み立てが必要な場合は Factory が担う。</p>
 */
@Component
public class ProductAdapter implements Adapter<Product, ProductEntity> {

    @Override
    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .deleteFlg(entity.getDeleteFlg())
                // category / stock は Factory が設定する
                .build();
    }

    @Override
    public ProductEntity fromDomain(Product domain) {
        if (domain == null) {
            return null;
        }
        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .price(domain.getPrice())
                .imageUrl(domain.getImageUrl())
                .productCategoryId(domain.getCategory() != null ? domain.getCategory().getId() : null)
                .deleteFlg(domain.getDeleteFlg())
                .build();
    }
}
