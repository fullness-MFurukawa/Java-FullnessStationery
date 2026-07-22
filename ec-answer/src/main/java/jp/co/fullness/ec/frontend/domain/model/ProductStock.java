package jp.co.fullness.ec.frontend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品在庫(product_stock)を表すドメインオブジェクト。
 * どの商品の在庫かは product_id で保持する(Product 集約の構成要素)。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock {

    /** 商品在庫ID */
    private Integer id;

    /** 在庫数 */
    private Integer quantity;

    /** 商品ID */
    private Integer productId;
}