package jp.co.fullness.ec.backend.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.backend.infrastructure.entity.CustomerEntity;

@Mapper
public interface CustomerMapper {
    List<CustomerEntity> selectAll();
    CustomerEntity selectById(Integer id);
}