package jp.co.fullness.ec.backend.presentation.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.backend.application.order.OrderStatusUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.presentation.form.OrderStatusUpdateForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/order/status/update")
@SessionAttributes("orderStatusUpdateForm")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusUpdateService orderStatusUpdateService;

    @ModelAttribute("orderStatusUpdateForm")
    public OrderStatusUpdateForm setUpForm() {
        return new OrderStatusUpdateForm();
    }

    /** BP016 入力: 購入履歴検索の[更新]から orderId を受け取り表示 */
    @GetMapping("/{orderId}")
    public String input(@PathVariable Integer orderId,
                        @ModelAttribute("orderStatusUpdateForm") OrderStatusUpdateForm form,
                        Model model) {
        Order order;
        try {
            order = orderStatusUpdateService.findById(orderId);
        } catch (DomainException e) {
            return "redirect:/admin/order/search";
        }
        form.setOrderId(orderId);
        form.setNewStatusId(null);
        model.addAttribute("order", order);
        model.addAttribute("statuses", orderStatusUpdateService.findAllStatuses());
        return "admin/order/status_input";
    }

    /** BP017 確認 */
    @PostMapping("/confirm")
    public String confirm(@Validated @ModelAttribute("orderStatusUpdateForm") OrderStatusUpdateForm form,
                          BindingResult bindingResult,
                          Model model) {
        Order order;
        try {
            order = orderStatusUpdateService.findById(form.getOrderId());
        } catch (DomainException e) {
            return "redirect:/admin/order/search";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("order", order);
            model.addAttribute("statuses", orderStatusUpdateService.findAllStatuses());
            return "admin/order/status_input";
        }
        OrderStatus newStatus = orderStatusUpdateService.resolveStatus(form.getNewStatusId());
        model.addAttribute("order", order);
        model.addAttribute("newStatus", newStatus);
        return "admin/order/status_confirm";
    }

    /** 更新実行 → BP018 完了へ */
    @PostMapping("/complete")
    public String execute(@ModelAttribute("orderStatusUpdateForm") OrderStatusUpdateForm form,
                          SessionStatus sessionStatus,
                          RedirectAttributes redirectAttributes) {
        try {
            OrderStatus newStatus = orderStatusUpdateService.resolveStatus(form.getNewStatusId());
            orderStatusUpdateService.updateStatus(form.getOrderId(), form.getNewStatusId());
            redirectAttributes.addFlashAttribute("orderNumber", form.getOrderId());
            redirectAttributes.addFlashAttribute("newStatusName", newStatus.getName());
            redirectAttributes.addFlashAttribute("updateDate", LocalDateTime.now());
        } catch (DomainException e) {
            sessionStatus.setComplete();
            return "redirect:/admin/order/search";
        }
        sessionStatus.setComplete();
        return "redirect:/admin/order/status/update/complete";
    }

    /** BP018 完了(直接アクセスは検索へ) */
    @GetMapping("/complete")
    public String complete(Model model) {
        if (!model.containsAttribute("orderNumber")) {
            return "redirect:/admin/order/search";
        }
        return "admin/order/status_complete";
    }
}