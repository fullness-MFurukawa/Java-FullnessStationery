package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;

/**
 * EmployeeAccountMapper のスライステスト(実DB接続)。
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EmployeeAccountMapperTest {

    @Autowired
    private EmployeeAccountMapper employeeAccountMapper;

    @Test
    void アカウント名で社員アカウントを取得できる() {
        EmployeeAccountEntity entity = employeeAccountMapper.selectByName("fullness");

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("fullness");
        assertThat(entity.getEmployeeId()).isEqualTo(1);
        // パスワードは BCrypt ハッシュ
        assertThat(entity.getPassword()).startsWith("$2b$");
    }

    @Test
    void 存在しないアカウント名はnullを返す() {
        EmployeeAccountEntity entity = employeeAccountMapper.selectByName("no_such_user");

        assertThat(entity).isNull();
    }
}