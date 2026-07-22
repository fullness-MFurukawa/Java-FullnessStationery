package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.ProductStock;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductStockEntity;

/**
 * ProductStock ⇄ ProductStockEntity の変換アダプタ。
 */
@Component
public class ProductStockAdapter implements Adapter<ProductStock, ProductStockEntity> {

    @Override
    public ProductStock toDomain(ProductStockEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProductStock.builder()
                .id(entity.getId())
                .quantity(entity.getQuantity())
                .productId(entity.getProductId())
                .build();
    }

    @Override
    public ProductStockEntity fromDomain(ProductStock domain) {
        if (domain == null) {
            return null;
        }
        return ProductStockEntity.builder()
                .id(domain.getId())
                .quantity(domain.getQuantity())
                .productId(domain.getProductId())
                .build();
    }
}