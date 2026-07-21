package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductStockMapper;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductRepository} の実装。検索(search/count)と登録(register)を実装。
 * findById/update/logicalDelete は商品修正・削除の実装時に対応する。
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final ProductStockMapper productStockMapper;
    private final ProductAdapter productAdapter;
    private final ProductStockAdapter productStockAdapter;

    @Override
    public List<Product> search(Integer categoryId, int limit, int offset) {
        return productAdapter.toDomainList(productMapper.search(categoryId, limit, offset));
    }

    @Override
    public long count(Integer categoryId) {
        return productMapper.count(categoryId);
    }

    @Override
    public void register(Product product) {
        // 商品を登録し、採番IDを反映
        ProductEntity productEntity = productAdapter.fromDomain(product);
        productMapper.insert(productEntity);
        product.setId(productEntity.getId());

        // 在庫を登録(商品IDを紐付け)
        ProductStock stock = product.getStock();
        if (stock != null) {
            stock.setProductId(productEntity.getId());
            ProductStockEntity stockEntity = productStockAdapter.fromDomain(stock);
            productStockMapper.insert(stockEntity);
            stock.setId(stockEntity.getId());
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        throw new UnsupportedOperationException("findById は未実装です。");
    }

    @Override
    public void update(Product product) {
        throw new UnsupportedOperationException("update は未実装です。");
    }

    @Override
    public void logicalDelete(Integer id) {
        throw new UnsupportedOperationException("logicalDelete は未実装です。");
    }
}