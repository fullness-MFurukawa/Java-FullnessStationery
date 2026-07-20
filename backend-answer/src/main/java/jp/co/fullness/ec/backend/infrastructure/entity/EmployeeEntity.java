package jp.co.fullness.ec.backend.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * employee テーブルの1行を表す Entity。
 * 外部キーは department_id を id のまま保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;

    /** kana */
    private String kana;

    /** department_id (外部キー) */
    private Integer departmentId;
}