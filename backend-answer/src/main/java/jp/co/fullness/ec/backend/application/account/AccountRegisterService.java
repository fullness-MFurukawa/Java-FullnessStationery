package jp.co.fullness.ec.backend.application.account;

import java.util.List;

import jp.co.fullness.ec.backend.domain.model.Employee;

/**
 * 担当者アカウント登録(UC009)の application サービス。
 */
public interface AccountRegisterService {

    /**
     * アカウント未作成の社員一覧を取得する(入力画面の選択肢用)。
     *
     * @return アカウント未作成の社員一覧
     */
    List<Employee> findRegisterableEmployees();

    /**
     * アカウント名の重複を判定する。
     *
     * @param accountName アカウント名
     * @return 既に使用されている場合 true
     */
    boolean isAccountNameDuplicated(String accountName);

    /**
     * 担当者アカウントを登録する。
     *
     * @param employeeId  社員ID
     * @param accountName アカウント名
     * @param rawPassword 平文パスワード
     */
    void register(Integer employeeId, String accountName, String rawPassword);
}
