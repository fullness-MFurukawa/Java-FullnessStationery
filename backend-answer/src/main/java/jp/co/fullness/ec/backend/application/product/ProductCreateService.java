package jp.co.fullness.ec.backend.application.product;

import java.util.List;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;

/**
 * 新商品登録(UC010)の application サービス。
 */
public interface ProductCreateService {

    /** 全カテゴリを取得する(カテゴリ選択プルダウン用)。 */
    List<ProductCategory> findAllCategories();

    /**
     * 画像を S3 にアップロードし、保存キーを返す(確認画面の前段で使用)。
     *
     * @param content         画像のバイト列
     * @param contentType     MIMEタイプ
     * @param originalFileName 元のファイル名(拡張子の抽出に使用)
     * @return S3 のオブジェクトキー
     */
    String uploadImage(byte[] content, String contentType, String originalFileName);

    /** オブジェクトキーから表示用の署名付きURLを生成する。 */
    String generateImageUrl(String key);

    /**
     * 商品(＋在庫)を登録する。
     *
     * @param command 登録内容
     */
    void register(ProductRegisterCommand command);
}