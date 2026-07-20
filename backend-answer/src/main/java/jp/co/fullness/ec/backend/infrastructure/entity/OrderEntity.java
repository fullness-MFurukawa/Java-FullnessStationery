package jp.co.fullness.ec.backend.infrastructure.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * orders テーブルの1行を表す Entity。
 * 外部キーは customer_id / order_status_id / payment_method_id を id のまま保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    /** id */
    private Integer id;

    /** order_date */
    private LocalDateTime orderDate;

    /** amount_total */
    private Integer amountTotal;

    /** customer_id (外部キー) */
    private Integer customerId;

    /** order_status_id (外部キー) */
    private Integer orderStatusId;

    /** payment_method_id (外部キー) */
    private Integer paymentMethodId;
}
