package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.EmployeeEntity;

/**
 * employee テーブルにアクセスする MyBatis マッパー。
 */
@Mapper
public interface EmployeeMapper {

    /** IDで社員を1件取得する。 */
    EmployeeEntity selectById(@Param("id") Integer id);

    /** アカウント未作成の社員を社員ID昇順で取得する。 */
    List<EmployeeEntity> selectWithoutAccount();
}
