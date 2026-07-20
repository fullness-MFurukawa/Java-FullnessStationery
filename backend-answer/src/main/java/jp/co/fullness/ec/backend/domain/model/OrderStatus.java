package jp.co.fullness.ec.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 注文ステータス(order_status)を表すドメインオブジェクト。
 * (注文済 / 入金済 / 配送中 / 完了)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {

    /** 注文ステータスID */
    private Integer id;

    /** 注文ステータス名 */
    private String name;
}
