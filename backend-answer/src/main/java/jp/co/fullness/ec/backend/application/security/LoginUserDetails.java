package jp.co.fullness.ec.backend.application.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;

/**
 * 認証済みの担当者を表す UserDetails 実装。
 * ドメインの {@link EmployeeAccount} を包み、Spring Security に必要な情報を提供する。
 */
public class LoginUserDetails implements UserDetails {

    private final EmployeeAccount account;

    public LoginUserDetails(EmployeeAccount account) {
        this.account = account;
    }

    /** 認証済みのドメイン集約を取得する。 */
    public EmployeeAccount getAccount() {
        return account;
    }

    /** 画面表示用の担当者名(メニューの「ようこそ○○さん」用)。 */
    public String getDisplayName() {
        return account.getEmployee() != null ? account.getEmployee().getName() : account.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 担当者(管理者)ロールを付与
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getName();
    }

    // アカウント状態(有効/失効/ロック等)は UserDetails の既定(すべて true)を使用する。
    // 5回失敗ロックは後段で AuthenticationFailureHandler と連携して実装する。
}