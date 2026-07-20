package jp.co.fullness.ec.backend.domain.factory;

import jp.co.fullness.ec.backend.domain.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * EmployeeAccount 集約を新規生成するためのソース(構成要素)。
 */
@Getter
@AllArgsConstructor
public class EmployeeAccountCreateSource {

    /** 対応する社員 */
    private final Employee employee;

    /** アカウント名 */
    private final String accountName;

    /** ハッシュ化済みパスワード */
    private final String hashedPassword;
}