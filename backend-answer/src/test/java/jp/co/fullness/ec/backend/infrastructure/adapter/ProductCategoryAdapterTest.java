package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;

class ProductCategoryAdapterTest {

    private final ProductCategoryAdapter adapter = new ProductCategoryAdapter();

    @Test
    void toDomainとfromDomainで往復しても一致する() {
        ProductCategoryEntity entity = new ProductCategoryEntity();
        entity.setId(1);
        entity.setName("文房具");

        ProductCategory domain = adapter.toDomain(entity);
        ProductCategoryEntity back = adapter.fromDomain(domain);

        assertThat(domain.getId()).isEqualTo(1);
        assertThat(domain.getName()).isEqualTo("文房具");
        assertThat(back.getName()).isEqualTo("文房具");
    }
}
