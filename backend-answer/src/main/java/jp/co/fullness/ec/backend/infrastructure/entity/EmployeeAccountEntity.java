package jp.co.fullness.ec.backend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * employee_account テーブルの1行を表す Entity。
 * 外部キーは employee_id を id のまま保持する。
 * password はハッシュ値(BCrypt)を保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAccountEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;

    /** password */
    private String password;

    /** employee_id (外部キー) */
    private Integer employeeId;
}