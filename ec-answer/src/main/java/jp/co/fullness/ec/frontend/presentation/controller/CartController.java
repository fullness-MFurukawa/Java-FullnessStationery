package jp.co.fullness.ec.frontend.presentation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.frontend.application.cart.CartService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.CartItem;
import jp.co.fullness.ec.frontend.domain.model.ProductStock;
import jp.co.fullness.ec.frontend.domain.repository.ProductStockRepository;
import jp.co.fullness.ec.frontend.presentation.form.CartForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductStockRepository productStockRepository;

    /** UC004 カートに入れる → カート画面へ。在庫超過は詳細へ戻してメッセージ。 */
    @PostMapping("/cart/add")
    public String add(@ModelAttribute CartForm form, RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(form.getProductId(), form.getQuantity());
        } catch (DomainException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products/detail/" + form.getProductId();
        }
        return "redirect:/purchase/input";
    }

    /** FP008 カート(購入入力)。空ならカテゴリ検索へ。 */
    @GetMapping("/purchase/input")
    public String cart(Model model) {
        if (cartService.getCart().isEmpty()) {
            return "redirect:/products/search";
        }
        // 各商品の現在在庫(数量プルダウンの上限・在庫変動チェック用)
        Map<Integer, Integer> stocks = new HashMap<>();
        boolean stockChanged = false;
        for (CartItem item : cartService.getCart().getItems()) {
            @SuppressWarnings("null")
            int stock = productStockRepository.findByProductId(item.getProductId())
                    .map(ProductStock::getQuantity).orElse(0);
            stocks.put(item.getProductId(), stock);
            if (item.getQuantity() > stock) {
                stockChanged = true;
            }
        }
        model.addAttribute("stocks", stocks);
        if (stockChanged) {
            model.addAttribute("stockMessage", "在庫数が変更されました");
        }
        return "purchase/input";
    }

    /** FP008 数量変更。 */
    @PostMapping("/cart/update")
    public String update(@ModelAttribute CartForm form, RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(form.getProductId(), form.getQuantity());
        } catch (DomainException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/purchase/input";
    }

    /** UC006 カートから削除。 */
    @PostMapping("/cart/remove")
    public String remove(@ModelAttribute CartForm form) {
        cartService.removeFromCart(form.getProductId());
        return "redirect:/purchase/input";
    }
}