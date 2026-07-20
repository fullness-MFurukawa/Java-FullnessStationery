package jp.co.fullness.ec.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.OrderStatus;

/**
 * 注文ステータス({@link OrderStatus})のリポジトリインターフェイス。
 */
public interface OrderStatusRepository {

    /**
     * 注文ステータスを全件、ID昇順で取得する(ステータス更新の選択肢用)。
     *
     * @return 注文ステータス一覧
     */
    List<OrderStatus> findAll();

    /**
     * IDで注文ステータスを取得する。
     *
     * @param id 注文ステータスID
     * @return 該当ステータス。存在しない場合は空
     */
    Optional<OrderStatus> findById(Integer id);
}
