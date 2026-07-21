package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.Department;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;

class EmployeeAdapterTest {

    private final EmployeeAdapter adapter = new EmployeeAdapter();

    @Test
    void toDomain_基本項目を変換し部署はnull() {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(1);
        entity.setName("フルネス太郎");
        entity.setKana("フルネスタロウ");
        entity.setDepartmentId(1);

        Employee domain = adapter.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1);
        assertThat(domain.getName()).isEqualTo("フルネス太郎");
        assertThat(domain.getKana()).isEqualTo("フルネスタロウ");
        assertThat(domain.getDepartment()).isNull();
    }

    @Test
    void fromDomain_部署IDを抽出する() {
        Employee employee = Employee.builder()
                .id(1)
                .name("フルネス太郎")
                .kana("フルネスタロウ")
                .department(Department.builder().id(1).build())
                .build();

        EmployeeEntity entity = adapter.fromDomain(employee);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getDepartmentId()).isEqualTo(1);
    }
}