package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import jp.co.fullness.ec.frontend.domain.model.OrderStatus;
import jp.co.fullness.ec.frontend.domain.repository.OrderStatusRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderStatusAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ OrderStatusRepositoryImpl.class, OrderStatusAdapter.class })
class OrderStatusRepositoryImplTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Test
    void findById_注文済を取得できる() {
        assertThat(orderStatusRepository.findById(1).orElseThrow().getName())
                .isEqualTo("注文済");
    }

    @Test
    void findAll_4件取得できる() {
        assertThat(orderStatusRepository.findAll()).hasSize(4);
    }
}