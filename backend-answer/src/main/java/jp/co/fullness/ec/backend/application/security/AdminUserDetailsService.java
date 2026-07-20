package jp.co.fullness.ec.backend.application.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;
import jp.co.fullness.ec.backend.domain.repository.EmployeeAccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * 担当者ログインの認証用に、アカウント名から担当者情報を読み込む UserDetailsService 実装。
 */
@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final EmployeeAccountRepository employeeAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeAccount account = employeeAccountRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりません: " + username));
        return new LoginUserDetails(account);
    }
}