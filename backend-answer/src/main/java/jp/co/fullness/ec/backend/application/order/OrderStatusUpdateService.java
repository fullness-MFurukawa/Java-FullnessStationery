package jp.co.fullness.ec.backend.application.order;

import java.util.List;

import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;

public interface OrderStatusUpdateService {

    /** 注文IDで注文を取得(存在しなければ DomainException)。BP016/BP017 表示用。 */
    Order findById(Integer orderId);

    /** ステータス選択肢(注文済→入金済→配送中→完了)。 */
    List<OrderStatus> findAllStatuses();

    /** 選択された新ステータスIDから OrderStatus を解決(確認画面の表示用)。 */
    OrderStatus resolveStatus(Integer statusId);

    /** 注文ステータスを更新(トランザクション制御)。 */
    void updateStatus(Integer orderId, Integer newStatusId);
}
