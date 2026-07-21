package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.repository.OrderStatusRepository;

@SpringBootTest
@Transactional
class OrderStatusRepositoryImplTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Test
    void findAll_ステータスをID昇順で取得できる() {
        List<OrderStatus> statuses = orderStatusRepository.findAll();

        assertThat(statuses).hasSizeGreaterThanOrEqualTo(4);
        assertThat(statuses.get(0).getName()).isEqualTo("注文済");
    }
}