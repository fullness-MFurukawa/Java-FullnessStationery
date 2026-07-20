package jp.co.fullness.ec.backend.infrastructure.factory;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.factory.EmployeeAccountCreateSource;
import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;

/**
 * EmployeeAccount 集約を新規生成するファクトリ実装。
 * 社員・アカウント名・ハッシュ済みパスワードから集約を組み立てる。
 */
@Component
public class EmployeeAccountCreateFactoryImpl
        implements Factory<EmployeeAccount, EmployeeAccountCreateSource> {

    @Override
    public EmployeeAccount create(EmployeeAccountCreateSource source) {
        return EmployeeAccount.builder()
                .name(source.getAccountName())
                .password(source.getHashedPassword())
                .employee(source.getEmployee())
                .build();
    }
}