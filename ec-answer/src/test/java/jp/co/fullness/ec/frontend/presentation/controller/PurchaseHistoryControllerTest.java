package jp.co.fullness.ec.frontend.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.frontend.application.order.OrderHistoryService;
import jp.co.fullness.ec.frontend.application.security.CustomerUserDetails;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.domain.model.Product;

@WebMvcTest(PurchaseHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderHistoryService orderHistoryService;
    @MockitoBean
    private Cart cart;   // GlobalModelAttributes 用

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder().id(100).name("テスト太郎").build();
        CustomerUserDetails principal = new CustomerUserDetails(customer);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void history_自分の注文一覧を表示() throws Exception {
        Order order = Order.builder()
                .id(1).orderDate(LocalDateTime.now()).amountTotal(300).build();
        when(orderHistoryService.findHistory(100)).thenReturn(List.of(order));

        mockMvc.perform(get("/purchase/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/history"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void history_履歴が無ければ空リストで表示() throws Exception {
        when(orderHistoryService.findHistory(100)).thenReturn(List.of());

        mockMvc.perform(get("/purchase/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/history"));
    }

    @Test
    void historyDetail_自分の注文なら詳細を表示() throws Exception {
        Product product = Product.builder().id(1).name("ボールペン").price(100).build();
        Order order = Order.builder()
                .id(5).orderDate(LocalDateTime.now()).amountTotal(200)
                .customer(Customer.builder().id(100).build())
                .details(List.of(OrderDetail.builder().product(product).count(2).build()))
                .build();
        when(orderHistoryService.findOwnedOrder(5, 100)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/purchase/history/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/history_detail"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void historyDetail_他人や無効IDなら一覧へリダイレクト() throws Exception {
        when(orderHistoryService.findOwnedOrder(9999, 100)).thenReturn(Optional.empty());

        mockMvc.perform(get("/purchase/history/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase/history"));
    }
}