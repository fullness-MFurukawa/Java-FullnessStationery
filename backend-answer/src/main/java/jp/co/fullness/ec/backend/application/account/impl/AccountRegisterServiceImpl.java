package jp.co.fullness.ec.backend.application.account.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.account.AccountRegisterService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.factory.EmployeeAccountCreateSource;
import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;
import jp.co.fullness.ec.backend.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;

/**
 * {@link AccountRegisterService} の実装。
 */
@Service
@RequiredArgsConstructor
public class AccountRegisterServiceImpl implements AccountRegisterService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAccountRepository employeeAccountRepository;
    private final Factory<EmployeeAccount, EmployeeAccountCreateSource> employeeAccountCreateFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Employee> findRegisterableEmployees() {
        return employeeRepository.findWithoutAccount();
    }

    @Override
    public boolean isAccountNameDuplicated(String accountName) {
        return employeeAccountRepository.existsByName(accountName);
    }

    @Override
    @Transactional
    public void register(Integer employeeId, String accountName, String rawPassword) {
        // 重複チェック
        if (employeeAccountRepository.existsByName(accountName)) {
            throw new DomainException("このアカウント名は既に使用されています");
        }
        // 社員を取得
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("選択された社員が存在しません"));
        // パスワードをハッシュ化
        String hashedPassword = passwordEncoder.encode(rawPassword);
        // Factory で集約を生成
        EmployeeAccount account = employeeAccountCreateFactory.create(
                new EmployeeAccountCreateSource(employee, accountName, hashedPassword));
        // 登録
        employeeAccountRepository.register(account);
    }
}
