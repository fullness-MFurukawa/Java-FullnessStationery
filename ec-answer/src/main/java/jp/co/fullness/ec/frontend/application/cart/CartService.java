package jp.co.fullness.ec.frontend.application.cart;

import jp.co.fullness.ec.frontend.domain.model.Cart;

public interface CartService {

    /** 現在のカート。 */
    Cart getCart();

    /** カートに追加(在庫超過は DomainException)。UC004。 */
    void addToCart(Integer productId, int quantity);

    /** 数量変更(在庫超過は DomainException)。FP008。 */
    void updateQuantity(Integer productId, int quantity);

    /** カートから削除。UC006。 */
    void removeFromCart(Integer productId);
}