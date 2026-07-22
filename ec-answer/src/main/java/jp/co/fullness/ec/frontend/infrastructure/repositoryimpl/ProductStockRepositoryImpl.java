package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.ProductStock;
import jp.co.fullness.ec.frontend.domain.repository.ProductStockRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.frontend.infrastructure.mapper.ProductStockMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockMapper productStockMapper;
    private final ProductStockAdapter productStockAdapter;

    @Override
    public Optional<ProductStock> findByProductId(Integer productId) {
        return Optional.ofNullable(productStockMapper.selectByProductId(productId))
                .map(productStockAdapter::toDomain);
    }

    @Override
    public Optional<ProductStock> lockByProductId(Integer productId) {
        return Optional.ofNullable(productStockMapper.lockByProductId(productId))
                .map(productStockAdapter::toDomain);
    }

    @Override
    public void decreaseQuantity(Integer productId, int amount) {
        productStockMapper.decreaseQuantity(productId, amount);
    }
}
