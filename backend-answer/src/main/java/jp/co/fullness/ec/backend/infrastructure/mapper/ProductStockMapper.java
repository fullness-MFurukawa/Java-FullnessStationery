package jp.co.fullness.ec.backend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;

/**
 * product_stock テーブルにアクセスする MyBatis マッパー。
 */
@Mapper
public interface ProductStockMapper {

    /** 在庫を新規登録する。生成IDは entity.id に設定される。 */
    void insert(ProductStockEntity entity);

    /** 商品IDで在庫を1件取得する。 */
    ProductStockEntity selectByProductId(@Param("productId") Integer productId);

    /** 商品IDで在庫数を更新する。 */
    void updateByProductId(ProductStockEntity entity);
}
