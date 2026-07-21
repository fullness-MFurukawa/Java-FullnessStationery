package jp.co.fullness.ec.backend.infrastructure.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.domain.model.Department;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.infrastructure.adapter.DepartmentAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAccountAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeAccountFactoryImplTest {

    @Mock
    private EmployeeAccountAdapter employeeAccountAdapter;

    @Mock
    private EmployeeAdapter employeeAdapter;

    @Mock
    private DepartmentAdapter departmentAdapter;

    @InjectMocks
    private EmployeeAccountFactoryImpl factory;

    @Test
    void create_Adapterの結果から集約を組み立て社員に部署を紐づける() {
        EmployeeAccountEntity accountEntity = new EmployeeAccountEntity();
        EmployeeEntity employeeEntity = new EmployeeEntity();
        DepartmentEntity departmentEntity = new DepartmentEntity();

        EmployeeAccount stubAccount = EmployeeAccount.builder().id(1).name("fullness").build();
        Employee stubEmployee = Employee.builder().id(1).name("フルネス太郎").build();
        Department stubDepartment = Department.builder().id(1).name("販売管理部").build();

        when(employeeAccountAdapter.toDomain(accountEntity)).thenReturn(stubAccount);
        when(employeeAdapter.toDomain(employeeEntity)).thenReturn(stubEmployee);
        when(departmentAdapter.toDomain(departmentEntity)).thenReturn(stubDepartment);

        // EmployeeAccountReconstructSource(account, employee, department) ← 実際のコンストラクタ順に合わせてください
        EmployeeAccountReconstructSource source =
                new EmployeeAccountReconstructSource(accountEntity, employeeEntity, departmentEntity);

        EmployeeAccount result = factory.create(source);

        assertThat(result).isSameAs(stubAccount);
        assertThat(result.getEmployee()).isSameAs(stubEmployee);
        // 社員に部署が紐づく
        assertThat(result.getEmployee().getDepartment()).isSameAs(stubDepartment);
    }

    @Test
    void create_sourceがnullならnullを返す() {
        assertThat(factory.create(null)).isNull();
    }

    @Test
    void create_accountがnullならnullを返す() {
        // 第1引数(account)を null にする想定。コンストラクタ順が異なる場合は合わせてください
        EmployeeAccountReconstructSource source =
                new EmployeeAccountReconstructSource(null, new EmployeeEntity(), new DepartmentEntity());

        assertThat(factory.create(source)).isNull();
    }
}