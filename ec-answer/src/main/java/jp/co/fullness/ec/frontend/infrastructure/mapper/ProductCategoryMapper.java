package jp.co.fullness.ec.frontend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.frontend.infrastructure.entity.ProductCategoryEntity;

@Mapper
public interface ProductCategoryMapper {
    List<ProductCategoryEntity> selectAll();
    ProductCategoryEntity selectById(Integer id);
}
