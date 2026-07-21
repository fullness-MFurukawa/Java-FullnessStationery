package jp.co.fullness.ec.backend.application.product;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;

/**
 * 商品修正(UC012)の application サービス。
 */
public interface ProductUpdateService {

    /** IDで商品(集約)を取得する(プリセット・確認表示用)。 */
    Optional<Product> findById(Integer id);

    /** 全カテゴリを取得する(カテゴリ選択プルダウン用)。 */
    List<ProductCategory> findAllCategories();

    /** 画像を S3 にアップロードし、保存キーを返す。 */
    String uploadImage(byte[] content, String contentType, String originalFileName);

    /** オブジェクトキーから表示用の署名付きURLを生成する。 */
    String generateImageUrl(String key);

    /** 商品(＋在庫)を更新する。 */
    void update(ProductUpdateCommand command);
}
