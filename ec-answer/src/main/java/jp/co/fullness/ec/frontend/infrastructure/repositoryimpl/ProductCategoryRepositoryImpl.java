package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.frontend.infrastructure.mapper.ProductCategoryMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {

    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryAdapter productCategoryAdapter;

    @Override
    public List<ProductCategory> findAll() {
        return productCategoryAdapter.toDomainList(productCategoryMapper.selectAll());
    }

    @Override
    public Optional<ProductCategory> findById(Integer id) {
        ProductCategoryEntity entity = productCategoryMapper.selectById(id);
        return Optional.ofNullable(entity).map(productCategoryAdapter::toDomain);
    }
}