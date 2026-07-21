package jp.co.fullness.ec.backend.application.product;

import java.util.List;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;

/**
 * 商品検索(UC011)の application サービス。
 */
public interface ProductSearchService {

    /** 全カテゴリを取得する(絞り込みプルダウン用)。 */
    List<ProductCategory> findAllCategories();

    /**
     * 商品を検索する(ページング)。
     *
     * @param categoryId カテゴリID(null で全件)
     * @param page       ページ番号(1始まり)
     * @param size       1ページの件数
     * @return 該当ページの商品一覧
     */
    List<Product> search(Integer categoryId, int page, int size);

    /** 検索条件に一致する商品件数を取得する(ページング用)。 */
    long count(Integer categoryId);

    /** オブジェクトキーから表示用の署名付きURLを生成する。 */
    String generateImageUrl(String key);
}