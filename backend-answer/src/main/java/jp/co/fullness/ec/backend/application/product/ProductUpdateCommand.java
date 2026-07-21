package jp.co.fullness.ec.backend.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品修正の入力をまとめたコマンド(DTO)。
 */
@Getter
@AllArgsConstructor
public class ProductUpdateCommand {

    /** 商品ID */
    private final Integer productId;

    /** 商品名 */
    private final String name;

    /** 単価 */
    private final Integer price;

    /** 在庫数 */
    private final Integer stockQuantity;

    /** カテゴリID */
    private final Integer categoryId;

    /** 画像の S3 キー(変更なしなら既存キー) */
    private final String imageKey;
}
