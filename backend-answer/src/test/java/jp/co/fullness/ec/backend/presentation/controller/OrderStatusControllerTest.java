package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.order.OrderStatusUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderDetail;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.presentation.form.OrderStatusUpdateForm;

@WebMvcTest(OrderStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatusUpdateService orderStatusUpdateService;

    /** status_input / status_confirm が参照する項目をすべて満たした注文。 */
    private Order fullOrder() {
        Product product = Product.builder().id(1).name("水性ボールペン(黒)").price(120).build();
        OrderDetail detail = new OrderDetail();
        detail.setCount(2);
        detail.setProduct(product);

        return Order.builder()
                .id(2)
                .orderDate(LocalDateTime.of(2024, 5, 12, 15, 30))
                .amountTotal(240)
                .customer(Customer.builder()
                        .username("testuser").name("テスト顧客")
                        .address1("東京都新宿区").address2("テストビル101")
                        .phoneNumber("090-1234-5678").build())
                .orderStatus(OrderStatus.builder().id(1).name("注文済").build())
                .details(List.of(detail))
                .build();
    }

    private List<OrderStatus> statuses() {
        return List.of(
                OrderStatus.builder().id(1).name("注文済").build(),
                OrderStatus.builder().id(2).name("入金済").build(),
                OrderStatus.builder().id(3).name("配送中").build(),
                OrderStatus.builder().id(4).name("完了").build());
    }

    @Test
    void input_注文が存在すれば入力画面を表示する() throws Exception {
        when(orderStatusUpdateService.findById(2)).thenReturn(fullOrder());
        when(orderStatusUpdateService.findAllStatuses()).thenReturn(statuses());

        mockMvc.perform(get("/admin/order/status/update/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/status_input"))
                .andExpect(model().attributeExists("order", "statuses", "orderStatusUpdateForm"));
    }

    @Test
    void input_注文が無ければ検索へリダイレクトする() throws Exception {
        when(orderStatusUpdateService.findById(999)).thenThrow(new DomainException("なし"));

        mockMvc.perform(get("/admin/order/status/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/order/search"));
    }

    @Test
    void confirm_ステータス未選択なら入力画面に戻す() throws Exception {
        when(orderStatusUpdateService.findById(2)).thenReturn(fullOrder());
        when(orderStatusUpdateService.findAllStatuses()).thenReturn(statuses());

        mockMvc.perform(post("/admin/order/status/update/confirm")
                        .param("orderId", "2")   // newStatusId 未指定 → バリデーションエラー
                        .sessionAttr("orderStatusUpdateForm", new OrderStatusUpdateForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/status_input"));
    }

    @Test
    void confirm_正常なら確認画面を表示する() throws Exception {
        when(orderStatusUpdateService.findById(2)).thenReturn(fullOrder());
        when(orderStatusUpdateService.resolveStatus(3))
                .thenReturn(OrderStatus.builder().id(3).name("配送中").build());

        mockMvc.perform(post("/admin/order/status/update/confirm")
                        .param("orderId", "2")
                        .param("newStatusId", "3")
                        .sessionAttr("orderStatusUpdateForm", new OrderStatusUpdateForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/status_confirm"))
                .andExpect(model().attributeExists("order", "newStatus"));
    }

    @Test
    void complete_直接アクセスは検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/order/status/update/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/order/search"));
    }
}