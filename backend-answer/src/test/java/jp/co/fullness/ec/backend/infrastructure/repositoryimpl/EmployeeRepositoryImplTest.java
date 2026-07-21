package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.repository.EmployeeRepository;

@SpringBootTest
@Transactional
class EmployeeRepositoryImplTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void findById_社員を取得できる() {
        Employee employee = employeeRepository.findById(1).orElseThrow();

        assertThat(employee).isNotNull();
        assertThat(employee.getName()).isEqualTo("フルネス太郎");
    }

    @SuppressWarnings("null")
    @Test
    void findWithoutAccount_アカウント未作成の社員のみ返す() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Integer newEmployeeId = jdbc.queryForObject(
                "INSERT INTO employee(name, kana, department_id) " +
                "VALUES ('テスト社員', 'テストシャイン', 1) RETURNING id",
                Integer.class);

        // ↓ 実際のメソッド名に合わせてください
        List<Employee> employees = employeeRepository.findWithoutAccount();

        assertThat(employees).extracting(Employee::getId).contains(newEmployeeId);
        assertThat(employees).extracting(Employee::getId).doesNotContain(1);
    }
}