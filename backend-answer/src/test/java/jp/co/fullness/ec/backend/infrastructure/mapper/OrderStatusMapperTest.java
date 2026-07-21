package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.backend.infrastructure.entity.OrderStatusEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderStatusMapperTest {

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Test
    void selectAll_ステータスをID昇順で取得できる() {
        List<OrderStatusEntity> statuses = orderStatusMapper.selectAll();

        assertThat(statuses).hasSizeGreaterThanOrEqualTo(4);
        assertThat(statuses.get(0).getName()).isEqualTo("注文済");
    }

    @Test
    void selectById_ステータスを1件取得できる() {
        OrderStatusEntity status = orderStatusMapper.selectById(3);

        assertThat(status).isNotNull();
        assertThat(status.getName()).isEqualTo("配送中");
    }
}