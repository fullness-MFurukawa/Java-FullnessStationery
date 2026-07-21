package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderEntity;

@Mapper
public interface OrderMapper {

    List<OrderEntity> search(@Param("condition") OrderSearchCondition condition,
                             @Param("limit") int limit,
                             @Param("offset") int offset);

    long count(@Param("condition") OrderSearchCondition condition);

    OrderEntity selectById(Integer id);

    void updateStatus(@Param("orderId") Integer orderId,
                      @Param("newStatusId") Integer newStatusId);

    List<LocalDate> selectDistinctOrderDates();
}