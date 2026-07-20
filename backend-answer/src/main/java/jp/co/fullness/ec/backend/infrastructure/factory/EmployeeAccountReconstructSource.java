package jp.co.fullness.ec.backend.infrastructure.factory;

import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeAccountEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * EmployeeAccount 集約を DB から復元するためのソース(構成要素の Entity 群)。
 */
@Getter
@AllArgsConstructor
public class EmployeeAccountReconstructSource {

    /** 社員アカウントの Entity */
    private final EmployeeAccountEntity account;

    /** 対応する社員の Entity */
    private final EmployeeEntity employee;

    /** 対応する部署の Entity */
    private final DepartmentEntity department;
}
