package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.backend.infrastructure.entity.CustomerEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @SuppressWarnings("null")
    @Test
    void selectAll_顧客を取得できる() {
        List<CustomerEntity> customers = customerMapper.selectAll();

        assertThat(customers).isNotEmpty();
        assertThat(customers).extracting(CustomerEntity::getUsername).contains("testuser");
    }

    @Test
    void selectById_顧客を1件取得できる() {
        CustomerEntity customer = customerMapper.selectById(1);

        assertThat(customer).isNotNull();
        assertThat(customer.getUsername()).isEqualTo("testuser");
    }
}