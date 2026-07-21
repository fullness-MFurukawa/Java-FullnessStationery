package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private DataSource dataSource;   

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
        // アカウント未作成の社員をトランザクション内で新規投入
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);   // import org.springframework.jdbc.core.JdbcTemplate;
        Integer employeeId = jdbc.queryForObject(
                "INSERT INTO employee(name, kana, department_id) " +
                "VALUES ('テスト花子', 'テストハナコ', 1) RETURNING id",
                Integer.class);

        // 既存データと衝突しない一意なアカウント名
        String accountName = "test_hanako_" + employeeId;

        Employee employee = Employee.builder().id(employeeId).build();
        EmployeeAccount account = EmployeeAccount.builder()
                .name(accountName)
                .password("$2b$10$dummyhashdummyhashdummyhashdummyhashdummyha")
                .employee(employee)
                .build();

        // 登録前は存在しない
        assertThat(employeeAccountRepository.existsByName(accountName)).isFalse();

        // 登録
        employeeAccountRepository.register(account);

        // 採番IDがドメインへ反映される
        assertThat(account.getId()).isNotNull();

        // 登録後は存在する
        assertThat(employeeAccountRepository.existsByName(accountName)).isTrue();

        // findByName で復元でき、社員が紐づく
        Optional<EmployeeAccount> found = employeeAccountRepository.findByName(accountName);
        assertThat(found).isPresent();
        assertThat(found.get().getEmployee()).isNotNull();
        assertThat(found.get().getEmployee().getId()).isEqualTo(employeeId);
        assertThat(found.get().getEmployee().getName()).isEqualTo("テスト花子");
    }
}