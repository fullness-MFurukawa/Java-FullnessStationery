package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductCategoryRepository} の実装。
 */
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
        return Optional.ofNullable(productCategoryMapper.selectById(id))
                .map(productCategoryAdapter::toDomain);
    }

    @Override
    public void register(ProductCategory category) {
        ProductCategoryEntity entity = productCategoryAdapter.fromDomain(category);
        productCategoryMapper.insert(entity);
        category.setId(entity.getId());
    }
}