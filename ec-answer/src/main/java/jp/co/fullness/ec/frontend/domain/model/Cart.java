package jp.co.fullness.ec.frontend.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/** 
 * セッション保持のカート(DBテーブルは持たない)。 
 */
@Getter
public class Cart implements Serializable {

    private final List<CartItem> items = new ArrayList<>();

    /** 追加。同一商品が既にあれば数量を加算する。 */
    public void add(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    /** 指定商品をカートから削除する(UC006)。 */
    public void remove(Integer productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    /** 合計金額。 */
    @SuppressWarnings("null")
    public int getTotalAmount() {
        return items.stream().mapToInt(CartItem::getSubtotal).sum();
    }

    /** 合計数量(ヘッダーのカートバッジ等に使用)。 */
    @SuppressWarnings("null")
    public int getTotalCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** 購入確定後などにカートを空にする。 */
    public void clear() {
        items.clear();
    }
}