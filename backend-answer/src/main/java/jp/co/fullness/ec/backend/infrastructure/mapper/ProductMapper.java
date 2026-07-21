package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;

/**
 * product テーブルにアクセスする MyBatis マッパー。
 */
@Mapper
public interface ProductMapper {

    /** 商品を検索する(論理削除除外・カテゴリ絞り込み・ページング)。 */
    List<ProductEntity> search(@Param("categoryId") Integer categoryId,
                               @Param("limit") int limit,
                               @Param("offset") int offset);

    /** 検索条件に一致する商品件数を取得する。 */
    long count(@Param("categoryId") Integer categoryId);

    /** 商品を新規登録する。生成IDは entity.id に設定される。 */
    void insert(ProductEntity entity);
}