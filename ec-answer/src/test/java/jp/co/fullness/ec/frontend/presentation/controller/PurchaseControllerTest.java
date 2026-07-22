package jp.co.fullness.ec.frontend.presentation.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.frontend.application.purchase.PurchaseService;
import jp.co.fullness.ec.frontend.application.security.CustomerUserDetails;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.CartItem;
import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;

@WebMvcTest(PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Cart cart;
    @MockitoBean
    private PurchaseService purchaseService;
    @MockitoBean
    private CustomerRepository customerRepository;
    @MockitoBean
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder()
                .id(100).name("テスト太郎").address1("東京都1-1").address2("101号室").build();
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
    void conform_カートが空なら購入入力へリダイレクト() throws Exception {
        when(cart.isEmpty()).thenReturn(true);

        mockMvc.perform(get("/purchase/conform"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase/input"));
    }

    @Test
    void conform_カートがあれば確認画面を表示() throws Exception {
        when(cart.isEmpty()).thenReturn(false);
        when(cart.getItems()).thenReturn(List.of(new CartItem(1, "ボールペン", 100, null, 2)));
        when(cart.getTotalAmount()).thenReturn(200);
        when(purchaseService.findPaymentMethods())
                .thenReturn(List.of(PaymentMethod.builder().id(1).name("現金").build()));
        when(customerRepository.findById(100))
                .thenReturn(Optional.of(Customer.builder()
                        .id(100).address1("東京都1-1").address2("101号室").build()));

        mockMvc.perform(get("/purchase/conform"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/conform"))
                .andExpect(model().attributeExists("paymentMethods", "shippingAddress"));
    }

    @Test
    void purchase_成功でカートを空にし完了へリダイレクト() throws Exception {
        when(cart.isEmpty()).thenReturn(false);
        when(purchaseService.placeOrder(100, 1)).thenReturn(555);

        mockMvc.perform(post("/purchase/conform")
                        .session(new MockHttpSession())
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase/complete"));

        verify(cart).clear();
    }

    @Test
    void purchase_在庫不足なら購入入力へ戻しエラー表示() throws Exception {
        when(cart.isEmpty()).thenReturn(false);
        when(purchaseService.placeOrder(eq(100), eq(1)))
                .thenThrow(new DomainException("申し訳ありませんが、商品「ボールペン」の在庫が不足しています"));

        mockMvc.perform(post("/purchase/conform")
                        .session(new MockHttpSession())
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase/input"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void purchase_支払い方法未選択なら確認画面にエラー表示() throws Exception {
        when(cart.isEmpty()).thenReturn(false);
        when(cart.getItems()).thenReturn(List.of(new CartItem(1, "ボールペン", 100, null, 2)));
        when(cart.getTotalAmount()).thenReturn(200);
        when(purchaseService.findPaymentMethods())
                .thenReturn(List.of(PaymentMethod.builder().id(1).name("現金").build()));
        when(customerRepository.findById(100))
                .thenReturn(Optional.of(Customer.builder().id(100).address1("東京都1-1").build()));

        mockMvc.perform(post("/purchase/conform")
                        .session(new MockHttpSession()))   // paymentMethodId なし
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/conform"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void complete_セッションフラグが無ければトップへ() throws Exception {
        mockMvc.perform(get("/purchase/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void complete_完了直後は完了画面を表示() throws Exception {
        Product product = Product.builder().id(1).name("ボールペン").price(100).build();
        Order order = Order.builder()
                .id(555).orderDate(LocalDateTime.now()).amountTotal(200)
                .paymentMethod(PaymentMethod.builder().id(1).name("現金").build())
                .details(List.of(OrderDetail.builder().product(product).count(2).build()))
                .build();
        when(orderRepository.findById(555)).thenReturn(Optional.of(order));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("purchaseCompletedOrderId", 555);

        mockMvc.perform(get("/purchase/complete").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/complete"))
                .andExpect(model().attributeExists("order"));
    }
}