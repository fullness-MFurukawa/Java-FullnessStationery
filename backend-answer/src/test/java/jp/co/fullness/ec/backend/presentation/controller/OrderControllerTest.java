package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.order.OrderSearchResult;
import jp.co.fullness.ec.backend.application.order.OrderSearchService;
import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderDetail;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.model.Product;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderSearchService orderSearchService;

    @Test
    void search_購入履歴一覧を表示する() throws Exception {
        // テンプレートが参照する項目(顧客・ステータス・明細+商品・日時・金額)を満たす
        Product product = Product.builder().id(1).name("水性ボールペン(黒)").price(120).build();
        OrderDetail detail = new OrderDetail();
        detail.setCount(2);
        detail.setProduct(product);

        Order order = Order.builder()
                .id(1)
                .orderDate(LocalDateTime.of(2024, 5, 12, 15, 30))
                .amountTotal(240)
                .customer(Customer.builder().username("testuser").name("テスト顧客").build())
                .orderStatus(OrderStatus.builder().id(4).name("完了").build())
                .details(List.of(detail))
                .build();

        when(orderSearchService.search(any(), any(), anyInt(), anyInt()))
                .thenReturn(new OrderSearchResult(List.of(order), 1, 1, 1));
        when(orderSearchService.findOrderDates()).thenReturn(List.of(LocalDate.of(2024, 5, 12)));
        when(orderSearchService.findCustomers()).thenReturn(List.of(Customer.builder().id(1).build()));

        mockMvc.perform(get("/admin/order/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/search"))
                .andExpect(model().attributeExists("orders", "totalPages", "currentPage", "orderDates", "customers"));
    }

    @Test
    void search_条件なしで0件なら未登録メッセージ() throws Exception {
        when(orderSearchService.search(any(), any(), anyInt(), anyInt()))
                .thenReturn(new OrderSearchResult(List.of(), 0, 0, 1));
        when(orderSearchService.findOrderDates()).thenReturn(List.of());
        when(orderSearchService.findCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/order/search"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("emptyMessage", "注文が登録されていません"));
    }

    @Test
    void search_条件付きで0件なら該当なしメッセージ() throws Exception {
        when(orderSearchService.search(any(), any(), anyInt(), anyInt()))
                .thenReturn(new OrderSearchResult(List.of(), 0, 0, 1));
        when(orderSearchService.findOrderDates()).thenReturn(List.of());
        when(orderSearchService.findCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/order/search").param("customerId", "1"))
                .andExpect(model().attribute("emptyMessage", "該当する注文が見つかりませんでした"));
    }
}
