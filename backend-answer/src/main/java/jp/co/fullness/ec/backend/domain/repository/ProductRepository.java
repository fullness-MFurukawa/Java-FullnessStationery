package jp.co.fullness.ec.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.Product;

/**
 * 商品({@link Product})集約のリポジトリインターフェイス。
 * 商品本体と在庫({@code product_stock})を集約として扱う。
 */
public interface ProductRepository {

    /**
     * 商品を検索する(商品検索 BP006)。
     * 論理削除済み(delete_flg=1)は除外し、商品ID昇順・ページングで取得する。
     *
     * @param categoryId カテゴリID。null の場合は全カテゴリ対象
     * @param limit      取得件数(1ページの件数)
     * @param offset     取得開始位置
     * @return 商品一覧(カテゴリ・在庫を含む)
     */
    List<Product> search(Integer categoryId, int limit, int offset);

    /**
     * 検索条件に一致する商品件数を取得する(ページング用)。
     *
     * @param categoryId カテゴリID。null の場合は全カテゴリ対象
     * @return 件数
     */
    long count(Integer categoryId);

    /**
     * IDで商品を取得する(カテゴリ・在庫を含む)。
     *
     * @param id 商品ID
     * @return 該当商品。存在しない場合は空
     */
    Optional<Product> findById(Integer id);

    /**
     * 商品を新規登録する(在庫も併せて登録)。新商品登録 UC010。
     *
     * @param product 登録する商品(在庫を含む)
     */
    void register(Product product);

    /**
     * 商品を更新する(在庫も併せて更新)。商品修正 UC012。
     *
     * @param product 更新する商品(在庫を含む)
     */
    void update(Product product);

    /**
     * 商品を論理削除する(delete_flg=1)。商品削除 UC013(物理削除は行わない)。
     *
     * @param id 商品ID
     */
    void logicalDelete(Integer id);
}
