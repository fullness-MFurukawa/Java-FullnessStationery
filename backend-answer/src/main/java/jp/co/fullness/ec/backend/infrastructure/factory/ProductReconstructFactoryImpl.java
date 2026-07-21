package jp.co.fullness.ec.backend.infrastructure.factory;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductStockAdapter;
import lombok.RequiredArgsConstructor;

/**
 * Product 集約(商品＋在庫＋カテゴリ)を Entity 群から復元するファクトリ実装。
 */
@Component
@RequiredArgsConstructor
public class ProductReconstructFactoryImpl implements Factory<Product, ProductReconstructSource> {

    private final ProductAdapter productAdapter;
    private final ProductStockAdapter productStockAdapter;
    private final ProductCategoryAdapter productCategoryAdapter;

    @Override
    public Product create(ProductReconstructSource source) {
        if (source == null || source.getProduct() == null) {
            return null;
        }
        Product product = productAdapter.toDomain(source.getProduct());
        product.setCategory(productCategoryAdapter.toDomain(source.getCategory()));
        product.setStock(productStockAdapter.toDomain(source.getStock()));
        return product;
    }
}
