package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.repository.CustomerRepository;

@SpringBootTest
@Transactional
class CustomerRepositoryImplTest {

    @Autowired
    private CustomerRepository customerRepository;

    @SuppressWarnings("null")
    @Test
    void findAll_顧客を取得できる() {
        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).isNotEmpty();
        assertThat(customers).extracting(Customer::getUsername).contains("testuser");
    }
}