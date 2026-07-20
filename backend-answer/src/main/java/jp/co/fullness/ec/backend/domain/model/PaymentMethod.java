package jp.co.fullness.ec.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 支払い方法(payment_method)を表すドメインオブジェクト。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    /** 支払い方法ID */
    private Integer id;

    /** 支払い方法名 */
    private String name;
}
