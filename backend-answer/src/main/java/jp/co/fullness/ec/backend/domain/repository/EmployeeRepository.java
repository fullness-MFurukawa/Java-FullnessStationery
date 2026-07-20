package jp.co.fullness.ec.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.backend.domain.model.Employee;

/**
 * 社員({@link Employee})のリポジトリインターフェイス。
 */
public interface EmployeeRepository {

    /**
     * アカウント未作成の社員一覧を社員ID昇順で取得する
     * (担当者アカウント登録 BP003 の選択肢用)。
     *
     * @return アカウント未作成の社員一覧
     */
    List<Employee> findWithoutAccount();

    /**
     * IDで社員を取得する。
     *
     * @param id 社員ID
     * @return 該当社員。存在しない場合は空
     */
    Optional<Employee> findById(Integer id);
}
