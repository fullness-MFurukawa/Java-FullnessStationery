package jp.co.fullness.ec.frontend.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jp.co.fullness.ec.frontend.application.order.OrderHistoryService;
import jp.co.fullness.ec.frontend.application.security.CustomerUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final OrderHistoryService orderHistoryService;

    /** FP011 購入履歴一覧。 */
    @GetMapping("/purchase/history")
    public String history(@AuthenticationPrincipal CustomerUserDetails user, Model model) {
        model.addAttribute("orders", orderHistoryService.findHistory(user.getCustomerId()));
        return "purchase/history";
    }

    /** FP012 購入履歴詳細(自分の注文のみ)。 */
    @GetMapping("/purchase/history/{orderId}")
    public String historyDetail(@PathVariable Integer orderId,
                                @AuthenticationPrincipal CustomerUserDetails user,
                                Model model) {
        return orderHistoryService.findOwnedOrder(orderId, user.getCustomerId())
                .map(order -> {
                    model.addAttribute("order", order);
                    return "purchase/history_detail";
                })
                .orElse("redirect:/purchase/history");   // 無効ID・他人の注文は一覧へ
    }
}