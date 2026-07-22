package jp.co.fullness.ec.frontend.infrastructure.factory;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.factory.Factory;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductStockAdapter;
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
