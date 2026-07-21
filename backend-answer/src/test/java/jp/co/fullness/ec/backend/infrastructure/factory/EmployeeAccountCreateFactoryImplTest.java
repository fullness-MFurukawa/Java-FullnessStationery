package jp.co.fullness.ec.backend.infrastructure.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.factory.EmployeeAccountCreateSource;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;

class EmployeeAccountCreateFactoryImplTest {

    private final EmployeeAccountCreateFactoryImpl factory = new EmployeeAccountCreateFactoryImpl();

    @Test
    void create_Sourceからアカウント集約を組み立てる() {
        Employee employee = Employee.builder().id(2).name("フルネス花子").build();

        EmployeeAccountCreateSource source =
                new EmployeeAccountCreateSource(employee, "hanako", "$2b$10$hashedhashedhashedhashedhashed");

        EmployeeAccount account = factory.create(source);

        assertThat(account.getName()).isEqualTo("hanako");
        assertThat(account.getPassword()).isEqualTo("$2b$10$hashedhashedhashedhashedhashed");
        assertThat(account.getEmployee()).isSameAs(employee);
    }
}