package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeAccountMapperTest {

    @Autowired
    private EmployeeAccountMapper employeeAccountMapper;

    @Test
    void selectByName_アカウントを取得できる() {
        EmployeeAccountEntity account = employeeAccountMapper.selectByName("fullness");

        assertThat(account).isNotNull();
        assertThat(account.getEmployeeId()).isEqualTo(1);
    }

    @Test
    void existsByName_存在有無を判定できる() {
        assertThat(employeeAccountMapper.existsByName("fullness")).isTrue();
        assertThat(employeeAccountMapper.existsByName("no_such_account")).isFalse();
    }
}