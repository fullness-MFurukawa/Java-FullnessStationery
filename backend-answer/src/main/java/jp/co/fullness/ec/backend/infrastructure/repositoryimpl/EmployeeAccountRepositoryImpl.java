package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.EmployeeAccountAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;
import jp.co.fullness.ec.backend.infrastructure.factory.EmployeeAccountReconstructSource;
import jp.co.fullness.ec.backend.infrastructure.mapper.DepartmentMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.EmployeeAccountMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;

/**
 * {@link EmployeeAccountRepository} の実装。
 */
@Repository
@RequiredArgsConstructor
public class EmployeeAccountRepositoryImpl implements EmployeeAccountRepository {

    private final EmployeeAccountMapper employeeAccountMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final EmployeeAccountAdapter employeeAccountAdapter;
    private final Factory<EmployeeAccount, EmployeeAccountReconstructSource> employeeAccountFactory;

    @Override
    public Optional<EmployeeAccount> findByName(String name) {
        // 1) アカウントを取得
        EmployeeAccountEntity accountEntity = employeeAccountMapper.selectByName(name);
        if (accountEntity == null) {
            return Optional.empty();
        }

        // 2) 対応する社員を取得
        EmployeeEntity employeeEntity = employeeMapper.selectById(accountEntity.getEmployeeId());

        // 3) 対応する部署を取得
        DepartmentEntity departmentEntity = null;
        if (employeeEntity != null && employeeEntity.getDepartmentId() != null) {
            departmentEntity = departmentMapper.selectById(employeeEntity.getDepartmentId());
        }

        // 4) Factory で集約に組み立て
        EmployeeAccount account = employeeAccountFactory.create(
                new EmployeeAccountReconstructSource(accountEntity, employeeEntity, departmentEntity));

        return Optional.of(account);
    }

    @Override
    public boolean existsByName(String name) {
        return employeeAccountMapper.existsByName(name);
    }

    @Override
    public void register(EmployeeAccount account) {
        // ドメイン → Entity へ変換して INSERT
        EmployeeAccountEntity entity = employeeAccountAdapter.fromDomain(account);
        employeeAccountMapper.insert(entity);
        // 採番されたIDをドメインへ反映
        account.setId(entity.getId());
    }
}