package jp.co.fullness.ec.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.adapter.Adapter;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;

/**
 * EmployeeAccount ⇄ EmployeeAccountEntity の変換アダプタ。
 * <p>toDomain では対応する社員(Employee)は設定しない
 * (Entity は employee_id しか持たないため)。Employee の組み立ては Factory が担う。</p>
 */
@Component
public class EmployeeAccountAdapter implements Adapter<EmployeeAccount, EmployeeAccountEntity> {

    @Override
    public EmployeeAccount toDomain(EmployeeAccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return EmployeeAccount.builder()
                .id(entity.getId())
                .name(entity.getName())
                .password(entity.getPassword())
                // employee は Factory が設定する
                .build();
    }

    @Override
    public EmployeeAccountEntity fromDomain(EmployeeAccount domain) {
        if (domain == null) {
            return null;
        }
        return EmployeeAccountEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .password(domain.getPassword())
                .employeeId(domain.getEmployee() != null ? domain.getEmployee().getId() : null)
                .build();
    }
}