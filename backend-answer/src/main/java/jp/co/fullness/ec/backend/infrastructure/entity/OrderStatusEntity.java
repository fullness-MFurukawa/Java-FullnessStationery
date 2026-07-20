package jp.co.fullness.ec.backend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * order_status テーブルの1行を表す Entity。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;
}
