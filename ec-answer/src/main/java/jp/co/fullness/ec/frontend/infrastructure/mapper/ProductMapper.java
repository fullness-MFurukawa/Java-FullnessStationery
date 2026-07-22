package jp.co.fullness.ec.frontend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.frontend.infrastructure.entity.ProductEntity;

@Mapper
public interface ProductMapper {
    /** カテゴリで検索(null=全件)、delete_flg=0 のみ。 */
    List<ProductEntity> searchByCategory(@Param("categoryId") Integer categoryId);

    ProductEntity selectById(Integer id);
}