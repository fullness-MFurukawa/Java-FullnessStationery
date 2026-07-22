package jp.co.fullness.ec.frontend.application.product;

import java.util.List;

import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.model.ProductCategory;

public interface ProductSearchService {

    /** カテゴリプルダウン用の全カテゴリ(ID昇順)。 */
    List<ProductCategory> findAllCategories();

    /** カテゴリで商品を検索(null=全商品)。FP006。 */
    List<Product> searchByCategory(Integer categoryId);

    /** 商品詳細を取得(存在しなければ DomainException)。FP007。 */
    Product findById(Integer id);

    /** 画像キーから署名付きURLを生成。 */
    String generateImageUrl(String imageKey);
}