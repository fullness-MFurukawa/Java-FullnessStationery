package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;

class EmployeeAccountAdapterTest {

    private final EmployeeAccountAdapter adapter = new EmployeeAccountAdapter();

    @Test
    void toDomain_基本項目を変換し社員はnull() {
        EmployeeAccountEntity entity = new EmployeeAccountEntity();
        entity.setId(1);
        entity.setName("fullness");
        entity.setPassword("hashed");
        entity.setEmployeeId(1);

        EmployeeAccount domain = adapter.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1);
        assertThat(domain.getName()).isEqualTo("fullness");
        assertThat(domain.getPassword()).isEqualTo("hashed");
        assertThat(domain.getEmployee()).isNull();
    }

    @Test
    void fromDomain_社員IDを抽出する() {
        EmployeeAccount account = EmployeeAccount.builder()
                .id(1)
                .name("fullness")
                .password("hashed")
                .employee(Employee.builder().id(1).build())
                .build();

        EmployeeAccountEntity entity = adapter.fromDomain(account);

        assertThat(entity.getEmployeeId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("fullness");
    }
}