package jp.co.fullness.ec.backend.domain.repository;

import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;

/**
 * 社員アカウント({@link EmployeeAccount})集約のリポジトリインターフェイス。
 *
 * <p>永続化技術に依存しないドメイン層のインターフェイスとして定義し、
 * 実装は infrastructure 層(MyBatis)で行う。</p>
 */
public interface EmployeeAccountRepository {

    /**
     * アカウント名で社員アカウントを取得する(担当者ログインで使用)。
     * 対応する社員・部署の情報も併せて取得する。
     *
     * @param name アカウント名
     * @return 該当する社員アカウント。存在しない場合は空
     */
    Optional<EmployeeAccount> findByName(String name);

    /**
     * 指定したアカウント名が既に登録済みかを判定する
     * (担当者アカウント登録の重複チェックで使用)。
     *
     * @param name アカウント名
     * @return 登録済みの場合 true
     */
    boolean existsByName(String name);

    /**
     * 社員アカウントを新規登録する(担当者アカウント登録で使用)。
     *
     * @param account 登録する社員アカウント
     */
    void register(EmployeeAccount account);
}
