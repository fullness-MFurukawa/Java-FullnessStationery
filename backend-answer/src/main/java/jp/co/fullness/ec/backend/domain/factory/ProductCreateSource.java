package jp.co.fullness.ec.backend.domain.factory;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Product 集約(商品＋在庫)を新規生成するためのソース。
 * imageKey は S3 のオブジェクトキー(image_url に保存する値)。
 */
@Getter
@AllArgsConstructor
public class ProductCreateSource {

    private final String name;
    private final Integer price;
    private final String imageKey;
    private final ProductCategory category;
    private final Integer stockQuantity;
}