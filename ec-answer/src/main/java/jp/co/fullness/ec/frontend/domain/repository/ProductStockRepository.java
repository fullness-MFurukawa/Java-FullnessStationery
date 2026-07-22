package jp.co.fullness.ec.frontend.domain.repository;

import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.ProductStock;

public interface ProductStockRepository {

    /** 在庫を取得(表示・数量チェック用)。 */
    Optional<ProductStock> findByProductId(Integer productId);

    /** 在庫を悲観的ロックで取得(SELECT ... FOR UPDATE)。UC005 購入確定。 */
    Optional<ProductStock> lockByProductId(Integer productId);

    /** 在庫を指定数だけ減らす。 */
    void decreaseQuantity(Integer productId, int amount);
}
