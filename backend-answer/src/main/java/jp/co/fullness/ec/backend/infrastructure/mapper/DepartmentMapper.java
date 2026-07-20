package jp.co.fullness.ec.backend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;

/**
 * department テーブルにアクセスする MyBatis マッパー。
 * SQL は DepartmentMapper.xml に定義する。
 */
@Mapper
public interface DepartmentMapper {

    /**
     * IDで部署を1件取得する。
     *
     * @param id 部署ID
     * @return 該当する Entity。存在しない場合は null
     */
    DepartmentEntity selectById(@Param("id") Integer id);
}