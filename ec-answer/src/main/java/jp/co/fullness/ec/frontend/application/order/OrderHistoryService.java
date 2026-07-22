package jp.co.fullness.ec.frontend.application.order;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.Order;

public interface OrderHistoryService {

    /** ログイン顧客の注文履歴を新しい順で取得。FP011。 */
    List<Order> findHistory(Integer customerId);

    /** 自分の注文のみ取得(他人の注文は空)。FP012。 */
    Optional<Order> findOwnedOrder(Integer orderId, Integer customerId);
}