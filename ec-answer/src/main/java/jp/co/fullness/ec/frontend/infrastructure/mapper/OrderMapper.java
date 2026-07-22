package jp.co.fullness.ec.frontend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.frontend.infrastructure.entity.OrderDetailEntity;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderEntity;

@Mapper
public interface OrderMapper {

    /** 注文を登録(採番IDを keyProperty=id で反映)。 */
    void insertOrder(OrderEntity entity);

    /** 注文明細を1件登録(採番IDを反映)。 */
    void insertOrderDetail(OrderDetailEntity entity);

    /** 注文を1件取得。 */
    OrderEntity selectById(Integer id);

    /** 顧客の注文を新しい順で取得。UC007 一覧。 */
    List<OrderEntity> selectByCustomerId(Integer customerId);

    /** 注文IDで明細を取得。 */
    List<OrderDetailEntity> selectDetailsByOrderId(Integer orderId);
}
