package jp.co.fullness.ec.frontend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.frontend.infrastructure.entity.OrderStatusEntity;

@Mapper
public interface OrderStatusMapper {
    List<OrderStatusEntity> selectAll();
    OrderStatusEntity selectById(Integer id);
}
