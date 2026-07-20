package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;

/**
 * EmployeeAccountRepository の existsByName / register の結合テスト。
 * INSERT を伴うため @Transactional でテスト後にロールバックする。
 */
@SpringBootTest
@Transactional
class EmployeeAccountRepositoryRegisterTest {

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    @Test
    void 既存アカウント名は存在すると判定される() {
        assertThat(employeeAccountRepository.existsByName("fullness")).isTrue();
    }

    @Test
    void 未使用アカウント名は存在しないと判定される() {
        assertThat(employeeAccountRepository.existsByName("no_such_user")).isFalse();
    }

    @Test
    void 社員アカウントを新規登録できる() {
        // アカウント未作成の社員(フルネス花子, id=2)に対して登録する
        Employee employee = Employee.builder().id(2).build();
        EmployeeAccount account = EmployeeAccount.builder()
                .name("hanako")
                .password("$2b$10$dummyhashdummyhashdummyhashdummyhashdummyha") // ハッシュ相当(長さのみ意味を持つ)
                .employee(employee)
                .build();

        // 登録前は存在しない
        assertThat(employeeAccountRepository.existsByName("hanako")).isFalse();

        // 登録
        employeeAccountRepository.register(account);

        // 採番IDがドメインへ反映される
        assertThat(account.getId()).isNotNull();

        // 登録後は存在する
        assertThat(employeeAccountRepository.existsByName("hanako")).isTrue();

        // findByName で復元でき、社員(フルネス花子)が紐づく
        Optional<EmployeeAccount> found = employeeAccountRepository.findByName("hanako");
        assertThat(found).isPresent();
        assertThat(found.get().getEmployee()).isNotNull();
        assertThat(found.get().getEmployee().getId()).isEqualTo(2);
        assertThat(found.get().getEmployee().getName()).isEqualTo("フルネス花子");
    }
}