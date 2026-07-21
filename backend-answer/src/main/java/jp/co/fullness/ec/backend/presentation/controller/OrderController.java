package jp.co.fullness.ec.backend.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.fullness.ec.backend.application.order.OrderSearchResult;
import jp.co.fullness.ec.backend.application.order.OrderSearchService;
import jp.co.fullness.ec.backend.presentation.form.OrderSearchForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class OrderController {

    private static final int PAGE_SIZE = 10;

    private final OrderSearchService orderSearchService;

    @GetMapping("/search")
    public String search(@ModelAttribute OrderSearchForm form, Model model) {

        int page = (form.getPage() == null) ? 1 : form.getPage();

        OrderSearchResult result =
                orderSearchService.search(form.getOrderDate(), form.getCustomerId(), page, PAGE_SIZE);

        model.addAttribute("orders", result.getOrders());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("orderDates", orderSearchService.findOrderDates());
        model.addAttribute("customers", orderSearchService.findCustomers());

        // 0件時のメッセージ(全件で0=未登録 / 条件付きで0=該当なし)
        if (result.getTotalCount() == 0) {
            boolean hasCondition = (form.getOrderDate() != null) || (form.getCustomerId() != null);
            model.addAttribute("emptyMessage",
                    hasCondition ? "該当する注文が見つかりませんでした" : "注文が登録されていません");
        }

        return "admin/order/search";
    }
}
