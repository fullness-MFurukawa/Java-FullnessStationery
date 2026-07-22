package jp.co.fullness.ec.frontend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.Order;

public interface OrderRepository {

    /** 注文+注文明細をまとめて登録(採番IDを反映)。UC005。 */
    void register(Order order);

    /** 顧客の購入履歴を新しい順で取得。UC007 一覧。 */
    List<Order> findByCustomerId(Integer customerId);

    /** 注文を1件取得(顧客・ステータス・明細+商品を含む)。UC007 詳細。 */
    Optional<Order> findById(Integer id);
}