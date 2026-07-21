package jp.co.fullness.ec.backend.infrastructure.factory;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.factory.ProductCreateSource;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductStock;

/**
 * Product 集約(商品＋在庫)を新規生成するファクトリ実装。
 */
@Component
public class ProductCreateFactoryImpl implements Factory<Product, ProductCreateSource> {

    @Override
    public Product create(ProductCreateSource source) {
        ProductStock stock = ProductStock.builder()
                .quantity(source.getStockQuantity())
                .build();
        return Product.builder()
                .name(source.getName())
                .price(source.getPrice())
                .imageUrl(source.getImageKey())   // S3 キーを image_url に保存
                .category(source.getCategory())
                .deleteFlg(0)
                .stock(stock)
                .build();
    }
}
