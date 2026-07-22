package jp.co.fullness.ec.frontend.domain.repository;

import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.Customer;

public interface CustomerRepository {

    /** 顧客を新規登録(採番IDを反映)。UC001。 */
    void register(Customer customer);

    /** メールアドレスで顧客を取得。UC002 ログイン認証。 */
    Optional<Customer> findByMailAddress(String mailAddress);

    /** 重複チェック(UC001)。 */
    boolean existsByMailAddress(String mailAddress);
    boolean existsByUsername(String username);

    Optional<Customer> findById(Integer id);
}