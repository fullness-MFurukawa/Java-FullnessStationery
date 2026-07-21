package jp.co.fullness.ec.backend.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.Order;

/**
 * 注文({@link Order})集約のリポジトリインターフェイス。
 * 注文本体と注文明細({@code orders_detail})を集約として扱う。
 */
public interface OrderRepository {

    /**
     * 注文を検索する(購入履歴検索 BP015)。
     * 購入日・顧客アカウント名で絞り込み(いずれも null 可＝条件なし)、
     * 注文ID降順(新しい順)・ページングで取得する。
     *
     * @param condition        購入履歴検索(BP015)の検索条件        
     * @param limit            取得件数(1ページの件数)
     * @param offset           取得開始位置
     * @return 注文一覧(注文明細・顧客・ステータス・支払い方法を含む)
     */
    List<Order> search(OrderSearchCondition condition, int limit, int offset);

    /**
     * 検索条件に一致する注文件数を取得する(ページング用)。
     *
     * @param condition        購入履歴検索(BP015)の検索条件  
     * @return 件数
     */
    long count(OrderSearchCondition condition);

    /**
     * IDで注文を取得する(注文明細・顧客・ステータス・支払い方法を含む)。
     * 注文ステータス更新 BP016 で使用。
     *
     * @param id 注文ID
     * @return 該当注文。存在しない場合は空
     */
    Optional<Order> findById(Integer id);

    /**
     * 注文ステータスを更新する(注文ステータス更新 UC016)。
     *
     * @param orderId       注文ID
     * @param orderStatusId 新しい注文ステータスID
     */
    void updateStatus(Integer orderId, Integer orderStatusId);

    /** 購入日プルダウン用: 注文が存在する日付(日単位)を新しい順で取得する。 */
    List<LocalDate> findDistinctOrderDates();
}
