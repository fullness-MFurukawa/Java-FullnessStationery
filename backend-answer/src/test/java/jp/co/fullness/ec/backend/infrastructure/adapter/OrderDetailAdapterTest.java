package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.OrderDetail;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderDetailEntity;

class OrderDetailAdapterTest {

    private final OrderDetailAdapter adapter = new OrderDetailAdapter();

    @Test
    void toDomain_countを変換しproductはnull() {
        OrderDetailEntity entity = new OrderDetailEntity();
        entity.setId(1);
        entity.setOrderId(1);
        entity.setProductId(5);
        entity.setCount(2);

        OrderDetail detail = adapter.toDomain(entity);

        assertThat(detail.getCount()).isEqualTo(2);
        assertThat(detail.getProduct()).isNull();
    }

    @Test
    void fromDomain_productIdとcountを抽出する() {
        OrderDetail detail = new OrderDetail();
        detail.setCount(2);
        detail.setProduct(Product.builder().id(5).build());

        OrderDetailEntity entity = adapter.fromDomain(detail);

        assertThat(entity.getProductId()).isEqualTo(5);
        assertThat(entity.getCount()).isEqualTo(2);
    }
}