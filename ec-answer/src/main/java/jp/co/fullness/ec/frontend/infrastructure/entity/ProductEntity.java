package jp.co.fullness.ec.frontend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * product テーブルの1行を表す Entity。
 * 外部キーは product_category_id を id のまま保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;

    /** price */
    private Integer price;

    /** image_url */
    private String imageUrl;

    /** product_category_id (外部キー) */
    private Integer productCategoryId;

    /** delete_flg (0:有効, 1:削除) */
    private Integer deleteFlg;
}
