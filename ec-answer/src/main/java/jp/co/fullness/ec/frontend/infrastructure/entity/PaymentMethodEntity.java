package jp.co.fullness.ec.frontend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * payment_method テーブルの1行を表す Entity。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;
}