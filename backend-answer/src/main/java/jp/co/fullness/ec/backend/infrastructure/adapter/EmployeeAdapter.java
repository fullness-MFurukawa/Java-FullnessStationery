package jp.co.fullness.ec.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.adapter.Adapter;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;

/**
 * Employee ⇄ EmployeeEntity の変換アダプタ。
 * <p>toDomain では所属部署(Department)は設定しない
 * (Entity は department_id しか持たないため)。Department の組み立ては Factory が担う。</p>
 */
@Component
public class EmployeeAdapter implements Adapter<Employee, EmployeeEntity> {

    @Override
    public Employee toDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }
        return Employee.builder()
                .id(entity.getId())
                .name(entity.getName())
                .kana(entity.getKana())
                // department は Factory が設定する
                .build();
    }

    @Override
    public EmployeeEntity fromDomain(Employee domain) {
        if (domain == null) {
            return null;
        }
        return EmployeeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .kana(domain.getKana())
                .departmentId(domain.getDepartment() != null ? domain.getDepartment().getId() : null)
                .build();
    }
}