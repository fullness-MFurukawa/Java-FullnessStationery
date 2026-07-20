package jp.co.fullness.ec.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 部署(department)を表すドメインオブジェクト。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    /** 部署ID */
    private Integer id;

    /** 部署名 */
    private String name;
}