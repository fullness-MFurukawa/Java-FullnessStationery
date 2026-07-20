package jp.co.fullness.ec.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;

/**
 * 商品カテゴリ({@link ProductCategory})のリポジトリインターフェイス。
 */
public interface ProductCategoryRepository {

    /**
     * 商品カテゴリを全件、カテゴリID昇順で取得する(検索・登録画面の選択肢用)。
     *
     * @return 商品カテゴリ一覧
     */
    List<ProductCategory> findAll();

    /**
     * IDで商品カテゴリを取得する。
     *
     * @param id 商品カテゴリID
     * @return 該当カテゴリ。存在しない場合は空
     */
    Optional<ProductCategory> findById(Integer id);

    /**
     * 商品カテゴリを新規登録する(商品カテゴリ登録 UC014)。
     *
     * @param category 登録するカテゴリ
     */
    void register(ProductCategory category);
}