package jp.co.fullness.ec.backend.infrastructure.factory;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.Department;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.infrastructure.adapter.DepartmentAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAccountAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAdapter;
import lombok.RequiredArgsConstructor;

/**
 * EmployeeAccount 集約を Entity 群から復元するファクトリ実装。
 * <p>汎用 {@link Factory} を実装し、注入した Adapter で各 Entity を
 * ドメイン部品へ変換したうえで、関連を組み立てて集約を構築する。</p>
 */
@Component
@RequiredArgsConstructor
public class EmployeeAccountFactoryImpl
        implements Factory<EmployeeAccount, EmployeeAccountReconstructSource> {

    private final EmployeeAccountAdapter employeeAccountAdapter;
    private final EmployeeAdapter employeeAdapter;
    private final DepartmentAdapter departmentAdapter;

    @Override
    public EmployeeAccount create(EmployeeAccountReconstructSource source) {
        if (source == null || source.getAccount() == null) {
            return null;
        }

        // 1.各 Entity をドメイン部品へ変換(この時点では関連は未設定)
        EmployeeAccount account = employeeAccountAdapter.toDomain(source.getAccount());
        Employee employee = employeeAdapter.toDomain(source.getEmployee());
        Department department = departmentAdapter.toDomain(source.getDepartment());

        // 2.関連をウィアリングして集約を組み立て
        if (employee != null) {
            employee.setDepartment(department);
        }
        account.setEmployee(employee);

        return account;
    }
}
