package jp.co.fullness.ec.backend.infrastructure.factory;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Product 集約を DB から復元するためのソース(構成要素の Entity 群)。
 */
@Getter
@AllArgsConstructor
public class ProductReconstructSource {

    private final ProductEntity product;
    private final ProductStockEntity stock;
    private final ProductCategoryEntity category;
}
