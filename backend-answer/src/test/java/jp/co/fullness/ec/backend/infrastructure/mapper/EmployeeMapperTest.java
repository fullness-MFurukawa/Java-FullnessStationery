package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DataSource dataSource;

    @Test
    void selectById_社員を取得できる() {
        EmployeeEntity employee = employeeMapper.selectById(1);

        assertThat(employee).isNotNull();
        assertThat(employee.getName()).isEqualTo("フルネス太郎");
    }

    @SuppressWarnings("null")
    @Test
    void selectWithoutAccount_アカウント未作成の社員のみ返す() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        // アカウント未作成の社員をトランザクション内で投入
        Integer newEmployeeId = jdbc.queryForObject(
                "INSERT INTO employee(name, kana, department_id) " +
                "VALUES ('テスト社員', 'テストシャイン', 1) RETURNING id",
                Integer.class);

        List<EmployeeEntity> employees = employeeMapper.selectWithoutAccount();

        // 投入した「アカウント未作成」の社員は含まれる
        assertThat(employees).extracting(EmployeeEntity::getId).contains(newEmployeeId);
        // アカウントを持つ社員1は含まれない
        assertThat(employees).extracting(EmployeeEntity::getId).doesNotContain(1);
    }
}