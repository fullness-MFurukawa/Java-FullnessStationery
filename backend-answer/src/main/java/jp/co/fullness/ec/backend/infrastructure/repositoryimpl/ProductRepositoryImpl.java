package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;
import jp.co.fullness.ec.backend.infrastructure.factory.ProductReconstructSource;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductCategoryMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductStockMapper;
import lombok.RequiredArgsConstructor;

/**
 * {@link ProductRepository} の実装。検索・登録・取得・更新を実装。
 * logicalDelete は商品削除の実装時に対応する。
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final ProductStockMapper productStockMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductAdapter productAdapter;
    private final ProductStockAdapter productStockAdapter;
    private final Factory<Product, ProductReconstructSource> productReconstructFactory;

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
        ProductEntity productEntity = productAdapter.fromDomain(product);
        productMapper.insert(productEntity);
        product.setId(productEntity.getId());

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
        ProductEntity productEntity = productMapper.selectById(id);
        // 存在しない、または論理削除済みは対象外
        if (productEntity == null
                || (productEntity.getDeleteFlg() != null && productEntity.getDeleteFlg() == 1)) {
            return Optional.empty();
        }
        ProductStockEntity stockEntity = productStockMapper.selectByProductId(id);
        ProductCategoryEntity categoryEntity = null;
        if (productEntity.getProductCategoryId() != null) {
            categoryEntity = productCategoryMapper.selectById(productEntity.getProductCategoryId());
        }
        Product product = productReconstructFactory.create(
                new ProductReconstructSource(productEntity, stockEntity, categoryEntity));
        return Optional.of(product);
    }

    @Override
    public void update(Product product) {
        ProductEntity productEntity = productAdapter.fromDomain(product);
        productMapper.update(productEntity);

        ProductStock stock = product.getStock();
        if (stock != null) {
            stock.setProductId(product.getId());
            ProductStockEntity stockEntity = productStockAdapter.fromDomain(stock);
            productStockMapper.updateByProductId(stockEntity);
        }
    }

    @Override
    public void logicalDelete(Integer id) {
         productMapper.logicalDelete(id);
    }
}