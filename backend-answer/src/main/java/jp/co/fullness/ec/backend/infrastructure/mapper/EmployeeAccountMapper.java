package jp.co.fullness.ec.backend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;

/**
 * employee_account テーブルにアクセスする MyBatis マッパー。
 * SQL は EmployeeAccountMapper.xml に定義する。
 */
@Mapper
public interface EmployeeAccountMapper {

    /**
     * アカウント名で社員アカウントを1件取得する(担当者ログインで使用)。
     */
    EmployeeAccountEntity selectByName(@Param("name") String name);

    /**
     * 指定アカウント名が既に存在するかを判定する(重複チェック用)。
     */
    boolean existsByName(@Param("name") String name);

    /**
     * 社員アカウントを新規登録する。生成されたIDは引数 entity の id に設定される。
     */
    void insert(EmployeeAccountEntity entity);
}