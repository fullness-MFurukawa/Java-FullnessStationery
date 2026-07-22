package jp.co.fullness.ec.frontend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * orders_detail テーブルの1行を表す Entity。
 * 外部キーは order_id / product_id を id のまま保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailEntity {

    /** id */
    private Integer id;

    /** order_id (外部キー) */
    private Integer orderId;

    /** product_id (外部キー) */
    private Integer productId;

    /** count */
    private Integer count;
}
