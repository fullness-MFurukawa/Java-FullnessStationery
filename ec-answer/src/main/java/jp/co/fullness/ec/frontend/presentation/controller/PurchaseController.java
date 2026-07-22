package jp.co.fullness.ec.frontend.presentation.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.frontend.application.security.CustomerUserDetails;
import jp.co.fullness.ec.frontend.application.purchase.PurchaseService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PurchaseController {

    /** 購入完了直後のみ完了画面を許可するためのセッションキー。 */
    private static final String COMPLETED_ORDER_ID = "purchaseCompletedOrderId";

    private final Cart cart;   // セッションスコープ
    private final PurchaseService purchaseService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    /** FP009 購入確認画面。 */
    @GetMapping("/purchase/conform")
    public String conform(@AuthenticationPrincipal CustomerUserDetails user, Model model) {
        if (cart.isEmpty()) {
            return "redirect:/purchase/input";
        }
        prepareConform(user, model);
        return "purchase/conform";
    }

    /** FP009 購入ボタン → 注文確定 → FP010。在庫不足は FP008 へ戻す。 */
    @PostMapping("/purchase/conform")
    public String purchase(@AuthenticationPrincipal CustomerUserDetails user,
                           @RequestParam(required = false) Integer paymentMethodId,
                           HttpSession session,
                           RedirectAttributes ra,
                           Model model) {

        if (cart.isEmpty()) {
            return "redirect:/purchase/input";
        }

        // 支払い方法 未選択チェック
        if (paymentMethodId == null) {
            prepareConform(user, model);
            model.addAttribute("errorMessage", "支払い方法を選択してください");
            return "purchase/conform";
        }

        try {
            Integer orderId = purchaseService.placeOrder(user.getCustomerId(), paymentMethodId);
            cart.clear();
            session.setAttribute(COMPLETED_ORDER_ID, orderId);
            return "redirect:/purchase/complete";
        } catch (DomainException e) {
            // 在庫不足など → 購入入力画面に戻してメッセージ表示
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/purchase/input";
        }
    }

    /** FP010 購入完了画面。直接アクセスはトップへ。 */
    @GetMapping("/purchase/complete")
    public String complete(HttpSession session, Model model) {
        Object orderId = session.getAttribute(COMPLETED_ORDER_ID);
        if (orderId == null) {
            return "redirect:/";   // 不正アクセス
        }
        session.removeAttribute(COMPLETED_ORDER_ID);

        Order order = orderRepository.findById((Integer) orderId).orElse(null);
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "purchase/complete";
    }

    /** 確認画面の表示データ(支払い方法・配送先)を組み立てる。 */
    private void prepareConform(CustomerUserDetails user, Model model) {
        model.addAttribute("paymentMethods", purchaseService.findPaymentMethods());
        customerRepository.findById(user.getCustomerId()).ifPresent(c -> {
            String address = c.getAddress1()
                    + (c.getAddress2() != null && !c.getAddress2().isBlank()
                        ? " " + c.getAddress2() : "");
            model.addAttribute("shippingAddress", address);
        });
    }
}