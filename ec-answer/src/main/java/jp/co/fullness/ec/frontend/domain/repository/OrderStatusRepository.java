package jp.co.fullness.ec.frontend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.OrderStatus;

public interface OrderStatusRepository {
    public List<OrderStatus> findAll();
    /** 注文確定時の初期ステータス「注文済」の取得などに使用。 */
    Optional<OrderStatus> findById(Integer id);
}