package jp.co.fullness.ec.backend.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;

@ExtendWith(MockitoExtension.class)
class AdminUserDetailsServiceTest {

    @Mock
    private EmployeeAccountRepository employeeAccountRepository;

    @InjectMocks
    private AdminUserDetailsService service;

    @Test
    void loadUserByUsername_アカウントが見つかればUserDetailsを返す() {
        EmployeeAccount account = EmployeeAccount.builder()
                .id(1)
                .name("fullness")
                .password("hashed-pass")
                .build();
        when(employeeAccountRepository.findByName("fullness")).thenReturn(Optional.of(account));

        UserDetails userDetails = service.loadUserByUsername("fullness");

        assertThat(userDetails.getUsername()).isEqualTo("fullness");
        assertThat(userDetails.getPassword()).isEqualTo("hashed-pass");
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_見つからなければUsernameNotFoundException() {
        when(employeeAccountRepository.findByName("no_such_account")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("no_such_account"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
