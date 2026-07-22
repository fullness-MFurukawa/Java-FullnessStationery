package jp.co.fullness.ec.frontend.application.cart.impl;

import org.springframework.stereotype.Service;

import jp.co.fullness.ec.frontend.application.cart.CartService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.CartItem;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final Cart cart;                       // セッションスコープ
    private final ProductRepository productRepository;

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void addToCart(Integer productId, int quantity) {
        Product product = loadProduct(productId);
        int stock = stockOf(product);

        if (quantity < 1) {
            throw new DomainException("数量を選択してください");
        }
        @SuppressWarnings("null")
        int alreadyInCart = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
        if (alreadyInCart + quantity > stock) {
            throw new DomainException("選択できる数量は" + stock + "までです");
        }

        cart.add(new CartItem(
                product.getId(), product.getName(), product.getPrice(),
                product.getImageUrl(), quantity));
    }

    @Override
    public void updateQuantity(Integer productId, int quantity) {
        Product product = loadProduct(productId);
        int stock = stockOf(product);

        if (quantity < 1 || quantity > stock) {
            throw new DomainException("選択できる数量は" + stock + "までです");
        }
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    @Override
    public void removeFromCart(Integer productId) {
        cart.remove(productId);
    }

    private Product loadProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DomainException("指定された商品は存在しません"));
    }

    private int stockOf(Product product) {
        return (product.getStock() != null) ? product.getStock().getQuantity() : 0;
    }
}
