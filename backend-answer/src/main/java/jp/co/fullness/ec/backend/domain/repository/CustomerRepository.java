package jp.co.fullness.ec.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.Customer;

/**
 * 顧客({@link Customer})のリポジトリインターフェイス。
 * バックエンドでは参照用途(購入履歴検索の絞り込み等)で利用する。
 */
public interface CustomerRepository {

    /**
     * 顧客を全件取得する(購入履歴検索 BP015 の顧客絞り込み選択肢用)。
     *
     * @return 顧客一覧
     */
    List<Customer> findAll();

    /**
     * IDで顧客を取得する。
     *
     * @param id 顧客ID
     * @return 該当顧客。存在しない場合は空
     */
    Optional<Customer> findById(Integer id);
}