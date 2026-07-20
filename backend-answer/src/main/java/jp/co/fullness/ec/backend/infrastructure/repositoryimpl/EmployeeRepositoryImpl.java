package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.repository.EmployeeRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.DepartmentAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;
import jp.co.fullness.ec.backend.infrastructure.mapper.DepartmentMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;

/**
 * {@link EmployeeRepository} の実装。
 */
@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final EmployeeAdapter employeeAdapter;
    private final DepartmentAdapter departmentAdapter;

    @Override
    public List<Employee> findWithoutAccount() {
        List<EmployeeEntity> entities = employeeMapper.selectWithoutAccount();
        // ドロップダウン表示用途のため部署は未設定(汎用 Adapter の一括変換を利用)
        return employeeAdapter.toDomainList(entities);
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        EmployeeEntity entity = employeeMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        Employee employee = employeeAdapter.toDomain(entity);
        // 部署も組み立てておく
        if (entity.getDepartmentId() != null) {
            DepartmentEntity deptEntity = departmentMapper.selectById(entity.getDepartmentId());
            employee.setDepartment(departmentAdapter.toDomain(deptEntity));
        }
        return Optional.of(employee);
    }
}
