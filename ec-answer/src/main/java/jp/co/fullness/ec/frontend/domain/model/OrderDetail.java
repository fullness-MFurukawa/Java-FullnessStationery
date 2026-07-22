package jp.co.fullness.ec.frontend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 注文明細(orders_detail)を表すドメインオブジェクト(Order 集約の構成要素)。
 * 対象商品({@link Product})を参照として保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {

    /** 注文明細ID */
    private Integer id;

    /** 注文ID */
    private Integer orderId;

    /** 対象商品 */
    private Product product;

    /** 購入数 */
    private Integer count;
}
