package jp.co.fullness.ec.backend.application.account.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.factory.EmployeeAccountCreateSource;
import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;
import jp.co.fullness.ec.backend.domain.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class AccountRegisterServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeAccountRepository employeeAccountRepository;

    @Mock
    private Factory<EmployeeAccount, EmployeeAccountCreateSource> employeeAccountCreateFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountRegisterServiceImpl service;

    @Test
    void findRegisterableEmployees_未作成社員を委譲して返す() {
        when(employeeRepository.findWithoutAccount())
                .thenReturn(List.of(Employee.builder().id(2).name("フルネス花子").build()));

        assertThat(service.findRegisterableEmployees()).hasSize(1);
    }

    @Test
    void isAccountNameDuplicated_存在有無を委譲する() {
        when(employeeAccountRepository.existsByName("fullness")).thenReturn(true);

        assertThat(service.isAccountNameDuplicated("fullness")).isTrue();
    }

    @Test
    void register_ハッシュ化しFactoryで生成して登録する() {
        Employee employee = Employee.builder().id(2).name("フルネス花子").build();
        EmployeeAccount created = EmployeeAccount.builder().id(10).name("hanako").build();

        when(employeeAccountRepository.existsByName("hanako")).thenReturn(false);
        when(employeeRepository.findById(2)).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode("raw-pass")).thenReturn("hashed-pass");
        when(employeeAccountCreateFactory.create(any())).thenReturn(created);

        service.register(2, "hanako", "raw-pass");

        // Factory へ渡した Source の中身を検証
        ArgumentCaptor<EmployeeAccountCreateSource> sourceCaptor =
                ArgumentCaptor.forClass(EmployeeAccountCreateSource.class);
        verify(employeeAccountCreateFactory).create(sourceCaptor.capture());
        assertThat(sourceCaptor.getValue().getEmployee()).isSameAs(employee);
        assertThat(sourceCaptor.getValue().getAccountName()).isEqualTo("hanako");
        assertThat(sourceCaptor.getValue().getHashedPassword()).isEqualTo("hashed-pass");

        // 生成された集約が登録される
        verify(employeeAccountRepository).register(created);
    }

    @Test
    void register_アカウント名が重複していれば例外で登録しない() {
        when(employeeAccountRepository.existsByName("dup")).thenReturn(true);

        assertThatThrownBy(() -> service.register(2, "dup", "raw-pass"))
                .isInstanceOf(DomainException.class);

        verify(employeeAccountRepository, never()).register(any());
    }

    @Test
    void register_社員が存在しなければ例外で登録しない() {
        when(employeeAccountRepository.existsByName("new")).thenReturn(false);
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(99, "new", "raw-pass"))
                .isInstanceOf(DomainException.class);

        verify(employeeAccountRepository, never()).register(any());
    }
}