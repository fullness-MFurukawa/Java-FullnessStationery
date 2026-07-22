package jp.co.fullness.ec.frontend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品カテゴリ(product_category)を表すドメインオブジェクト。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    /** 商品カテゴリID */
    private Integer id;

    /** 商品カテゴリ名 */
    private String name;
}