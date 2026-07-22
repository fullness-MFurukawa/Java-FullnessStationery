package jp.co.fullness.ec.frontend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.frontend.infrastructure.entity.ProductStockEntity;

@Mapper
public interface ProductStockMapper {
    ProductStockEntity selectByProductId(Integer productId);

    /** 悲観的ロック(SELECT ... FOR UPDATE)。UC005。 */
    ProductStockEntity lockByProductId(Integer productId);

    void decreaseQuantity(@Param("productId") Integer productId, @Param("amount") int amount);
}