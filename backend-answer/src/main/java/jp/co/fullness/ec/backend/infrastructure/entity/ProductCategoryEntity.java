package jp.co.fullness.ec.backend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * product_category テーブルの1行を表す Entity。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;
}
