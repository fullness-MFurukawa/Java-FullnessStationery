package jp.co.fullness.ec.frontend.presentation.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchForm {
    /** カテゴリID(null=全商品)。 */
    private Integer categoryId;
}
