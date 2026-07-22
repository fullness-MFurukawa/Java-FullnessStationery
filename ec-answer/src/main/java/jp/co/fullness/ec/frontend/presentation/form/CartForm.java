package jp.co.fullness.ec.frontend.presentation.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartForm {
    private Integer productId;
    private Integer quantity;   // 削除時は未使用
}