package jp.co.fullness.ec.backend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;

/**
 * product_stock テーブルにアクセスする MyBatis マッパー。
 */
@Mapper
public interface ProductStockMapper {

    /** 在庫を新規登録する。生成IDは entity.id に設定される。 */
    void insert(ProductStockEntity entity);
}
