package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.Department;
import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;

class DepartmentAdapterTest {

    private final DepartmentAdapter adapter = new DepartmentAdapter();

    @Test
    void toDomainとfromDomainで往復しても一致する() {
        DepartmentEntity entity = new DepartmentEntity();
        entity.setId(1);
        entity.setName("販売管理部");

        Department domain = adapter.toDomain(entity);
        DepartmentEntity back = adapter.fromDomain(domain);

        assertThat(domain.getId()).isEqualTo(1);
        assertThat(domain.getName()).isEqualTo("販売管理部");
        assertThat(back.getId()).isEqualTo(1);
        assertThat(back.getName()).isEqualTo("販売管理部");
    }
}