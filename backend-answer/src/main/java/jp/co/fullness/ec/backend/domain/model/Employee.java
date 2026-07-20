package jp.co.fullness.ec.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 社員(employee)を表すドメインオブジェクト。
 * 所属部署({@link Department})を参照として保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    /** 社員ID */
    private Integer id;

    /** 社員名 */
    private String name;

    /** 社員名カナ */
    private String kana;

    /** 所属部署 */
    private Department department;
}
