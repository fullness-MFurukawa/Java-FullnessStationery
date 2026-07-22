package jp.co.fullness.ec.frontend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.frontend.infrastructure.entity.PaymentMethodEntity;

@Mapper
public interface PaymentMethodMapper {
    List<PaymentMethodEntity> selectAll();
    PaymentMethodEntity selectById(Integer id);
}