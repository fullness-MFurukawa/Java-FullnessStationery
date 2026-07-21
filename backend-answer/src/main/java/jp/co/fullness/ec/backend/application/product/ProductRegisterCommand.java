package jp.co.fullness.ec.backend.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 新商品登録の入力をまとめたコマンド(DTO)。
 */
@Getter
@AllArgsConstructor
public class ProductRegisterCommand {

    /** 商品名 */
    private final String name;

    /** 単価 */
    private final Integer price;

    /** 在庫数 */
    private final Integer stockQuantity;

    /** カテゴリID */
    private final Integer categoryId;

    /** 画像の S3 キー(image_url に保存) */
    private final String imageKey;
}
