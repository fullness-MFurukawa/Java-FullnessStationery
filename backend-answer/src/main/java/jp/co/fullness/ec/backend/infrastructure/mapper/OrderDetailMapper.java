package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.backend.infrastructure.entity.OrderDetailEntity;

@Mapper
public interface OrderDetailMapper {
    List<OrderDetailEntity> selectByOrderId(Integer orderId);
}
