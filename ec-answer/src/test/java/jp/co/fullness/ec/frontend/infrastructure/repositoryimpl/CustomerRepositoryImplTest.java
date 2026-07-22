package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.CustomerAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ CustomerRepositoryImpl.class, CustomerAdapter.class })
class CustomerRepositoryImplTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void register_登録で採番IDが反映されfindByMailAddressで取得できる() {
        Customer customer = baseCustomer("suzuki@example.com", "suzuki");

        customerRepository.register(customer);

        assertThat(customer.getId()).isNotNull();
        Customer found = customerRepository.findByMailAddress("suzuki@example.com").orElseThrow();
        assertThat(found.getId()).isEqualTo(customer.getId());
        assertThat(found.getNameKana()).isEqualTo("スズキハナコ");
    }

    @Test
    void findByMailAddress_該当なしはempty() {
        assertThat(customerRepository.findByMailAddress("none@example.com")).isEmpty();
    }

    @Test
    void existsByMailAddress_登録後にtrueになり未登録はfalse() {
        customerRepository.register(baseCustomer("dup@example.com", "dupuser"));

        assertThat(customerRepository.existsByMailAddress("dup@example.com")).isTrue();
        assertThat(customerRepository.existsByMailAddress("none@example.com")).isFalse();
    }

    @Test
    void existsByUsername_登録後にtrueになり未登録はfalse() {
        customerRepository.register(baseCustomer("u2@example.com", "uniqueuser"));

        assertThat(customerRepository.existsByUsername("uniqueuser")).isTrue();
        assertThat(customerRepository.existsByUsername("nouser")).isFalse();
    }

    @Test
    void findById_登録後にIDで取得できる() {
        Customer c = baseCustomer("byid@example.com", "byiduser");
        customerRepository.register(c);

        assertThat(customerRepository.findById(c.getId()).orElseThrow().getMailAddress())
                .isEqualTo("byid@example.com");
    }

    private Customer baseCustomer(String mail, String username) {
        return Customer.builder()
                .name("鈴木花子")
                .nameKana("スズキハナコ")
                .address1("大阪府2-2")
                .phoneNumber("08033334444")
                .mailAddress(mail)
                .username(username)
                .password("$2a$10$dummyhashdummyhashdummyha")
                .build();
    }
}
