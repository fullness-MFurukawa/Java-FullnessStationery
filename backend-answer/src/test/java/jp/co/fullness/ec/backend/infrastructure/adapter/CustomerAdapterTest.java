package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.infrastructure.entity.CustomerEntity;

class CustomerAdapterTest {

    private final CustomerAdapter adapter = new CustomerAdapter();

    @Test
    void toDomainとfromDomainで往復しても一致する() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1);
        entity.setName("テスト顧客");
        entity.setAddress1("東京都新宿区");
        entity.setAddress2("テストビル101");
        entity.setPhoneNumber("090-1234-5678");
        entity.setMailAddress("testuser@example.com");
        entity.setUsername("testuser");
        entity.setPassword("hashed");
        entity.setCreatedAt(LocalDateTime.of(2024, 5, 1, 9, 0));

        Customer domain = adapter.toDomain(entity);
        CustomerEntity back = adapter.fromDomain(domain);

        assertThat(domain.getUsername()).isEqualTo("testuser");
        assertThat(domain.getName()).isEqualTo("テスト顧客");
        assertThat(back.getId()).isEqualTo(1);
        assertThat(back.getMailAddress()).isEqualTo("testuser@example.com");
        assertThat(back.getPhoneNumber()).isEqualTo("090-1234-5678");
    }
}
