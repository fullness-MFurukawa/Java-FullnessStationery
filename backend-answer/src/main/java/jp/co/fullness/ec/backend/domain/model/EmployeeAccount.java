package jp.co.fullness.ec.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 社員アカウント(employee_account)を表すドメインオブジェクト。
 * どの社員({@link Employee})のアカウントかを参照として保持する。
 * password はハッシュ値(BCrypt)を保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAccount {

    /** アカウントID */
    private Integer id;

    /** アカウント名 */
    private String name;

    /** パスワード(ハッシュ値) */
    private String password;

    /** 対応する社員 */
    private Employee employee;
}
