package jp.co.fullness.ec.frontend.application.customer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.presentation.form.CustomerRegisterForm;

@ExtendWith(MockitoExtension.class)
class CustomerRegisterServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerRegisterServiceImpl customerRegisterService;

    @Test
    void isMailAddressTaken_リポジトリに委譲する() {
        when(customerRepository.existsByMailAddress("a@example.com")).thenReturn(true);
        assertThat(customerRegisterService.isMailAddressTaken("a@example.com")).isTrue();
    }

    @Test
    void isUsernameTaken_リポジトリに委譲する() {
        when(customerRepository.existsByUsername("taro")).thenReturn(false);
        assertThat(customerRegisterService.isUsernameTaken("taro")).isFalse();
    }

    @Test
    void register_パスワードをハッシュ化して顧客を登録する() {
        CustomerRegisterForm form = new CustomerRegisterForm();
        form.setName("山田太郎");
        form.setNameKana("ヤマダタロウ");
        form.setAddress1("東京都1-1");
        form.setAddress2("101号室");
        form.setPhoneNumber("090-1111-2222");
        form.setMailAddress("yamada@example.com");
        form.setUsername("yamada");
        form.setPassword("pass1");

        when(passwordEncoder.encode("pass1")).thenReturn("HASHED_VALUE");

        customerRegisterService.register(form);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).register(captor.capture());
        Customer saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("HASHED_VALUE");   // 平文でない
        assertThat(saved.getNameKana()).isEqualTo("ヤマダタロウ");
        assertThat(saved.getMailAddress()).isEqualTo("yamada@example.com");
    }
}