package jp.co.fullness.ec.frontend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * product_stock テーブルの1行を表す Entity。
 * 外部キーは product_id を id のまま保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockEntity {

    /** id */
    private Integer id;

    /** quantity */
    private Integer quantity;

    /** product_id (外部キー) */
    private Integer productId;
}