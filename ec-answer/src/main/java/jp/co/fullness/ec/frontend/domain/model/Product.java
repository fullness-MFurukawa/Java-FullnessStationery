package jp.co.fullness.ec.frontend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品(product)を表すドメインオブジェクト(Product 集約のルート)。
 * 所属カテゴリ({@link ProductCategory})と在庫({@link ProductStock})を
 * 参照として保持する。delete_flg により論理削除を表す(物理削除は行わない)。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /** 商品ID */
    private Integer id;

    /** 商品名 */
    private String name;

    /** 価格 */
    private Integer price;

    /** 画像URL */
    private String imageUrl;

    /** 商品カテゴリ */
    private ProductCategory category;

    /** 削除フラグ(0:有効, 1:削除) */
    private Integer deleteFlg;

    /** 在庫情報 */
    private ProductStock stock;

    /**
     * 論理削除済みかどうかを返す。
     *
     * @return delete_flg が 1 の場合 true
     */
    public boolean isDeleted() {
        return deleteFlg != null && deleteFlg == 1;
    }
}
