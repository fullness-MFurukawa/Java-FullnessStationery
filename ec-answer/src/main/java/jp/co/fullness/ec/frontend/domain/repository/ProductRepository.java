package jp.co.fullness.ec.frontend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.Product;

public interface ProductRepository {

    /** カテゴリで商品を検索(null=全件)。delete_flg=0 のみ。UC003。 */
    List<Product> searchByCategory(Integer categoryId);

    /** 商品を1件取得(在庫・カテゴリを含む)。UC004 商品詳細/カート。 */
    Optional<Product> findById(Integer id);
}