package jp.co.fullness.ec.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.adapter.Adapter;
import jp.co.fullness.ec.backend.domain.model.Department;
import jp.co.fullness.ec.backend.infrastructure.entity.DepartmentEntity;

/**
 * Department ⇄ DepartmentEntity の変換アダプタ。
 */
@Component
public class DepartmentAdapter implements Adapter<Department, DepartmentEntity> {

    @Override
    public Department toDomain(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return Department.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public DepartmentEntity fromDomain(Department domain) {
        if (domain == null) {
            return null;
        }
        return DepartmentEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}