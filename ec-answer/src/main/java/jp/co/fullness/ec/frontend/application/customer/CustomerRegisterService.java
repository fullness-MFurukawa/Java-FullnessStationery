package jp.co.fullness.ec.frontend.application.customer;

import jp.co.fullness.ec.frontend.presentation.form.CustomerRegisterForm;

public interface CustomerRegisterService {

    /** メールアドレスが既に登録済みか。UC001 重複チェック。 */
    boolean isMailAddressTaken(String mailAddress);

    /** アカウント名が既に使用済みか。UC001 重複チェック。 */
    boolean isUsernameTaken(String username);

    /** 顧客を新規登録(パスワードはBCryptハッシュ化)。UC001。 */
    void register(CustomerRegisterForm form);
}