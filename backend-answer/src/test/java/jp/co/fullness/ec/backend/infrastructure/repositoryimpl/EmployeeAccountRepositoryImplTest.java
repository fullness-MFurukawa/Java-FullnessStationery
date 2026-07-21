package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;

@SpringBootTest
@Transactional
class EmployeeAccountRepositoryImplTest {

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    @Test
    void findByName_アカウントと社員を組み立てて返す() {
        // 戻り値が Optional の場合は .orElseThrow() で受けてください
        EmployeeAccount account = employeeAccountRepository.findByName("fullness")
            .orElseThrow();

        assertThat(account).isNotNull();
        assertThat(account.getName()).isEqualTo("fullness");
        // Factory による社員の組み立てを検証
        assertThat(account.getEmployee()).isNotNull();
        assertThat(account.getEmployee().getName()).isEqualTo("フルネス太郎");
    }

    @Test
    void existsByName_存在有無を判定できる() {
        assertThat(employeeAccountRepository.existsByName("fullness")).isTrue();
        assertThat(employeeAccountRepository.existsByName("no_such_account")).isFalse();
    }
}