package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;

/**
 * product_category テーブルにアクセスする MyBatis マッパー。
 */
@Mapper
public interface ProductCategoryMapper {

    /** 全カテゴリをID昇順で取得する。 */
    List<ProductCategoryEntity> selectAll();

    /** IDでカテゴリを1件取得する。 */
    ProductCategoryEntity selectById(@Param("id") Integer id);

    /** カテゴリを新規登録する。生成IDは entity.id に設定される。 */
    void insert(ProductCategoryEntity entity);
}
