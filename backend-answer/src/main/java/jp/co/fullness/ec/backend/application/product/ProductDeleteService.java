package jp.co.fullness.ec.backend.application.product;

import jp.co.fullness.ec.backend.domain.model.Product;

public interface ProductDeleteService {

    /** 削除確認画面の表示用に商品を取得(存在しない/削除済みは例外)。 */
    Product findById(Integer id);

    /** 画像キーから表示用URLを生成(検索画面と同一ロジックを再利用)。 */
    String generateImageUrl(String imageKey);

    /** 論理削除を実行する。 */
    void delete(Integer id);
}