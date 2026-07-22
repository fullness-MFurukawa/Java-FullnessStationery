package jp.co.fullness.ec.frontend.domain.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 
 * カート内の1商品(商品IDと数量、表示用の情報を保持)。 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private Integer productId;
    private String productName;
    private Integer price;
    private String imageUrl;   // S3キー(表示時に署名付きURL化)
    private int quantity;

    /** 小計 = 単価 × 数量 */
    public int getSubtotal() {
        return (price == null ? 0 : price) * quantity;
    }
}
