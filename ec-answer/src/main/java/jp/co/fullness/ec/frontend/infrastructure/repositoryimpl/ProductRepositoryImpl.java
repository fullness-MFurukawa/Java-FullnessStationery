package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.factory.Factory;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.frontend.infrastructure.entity.ProductStockEntity;
import jp.co.fullness.ec.frontend.infrastructure.factory.ProductReconstructSource;
import jp.co.fullness.ec.frontend.infrastructure.mapper.ProductCategoryMapper;
import jp.co.fullness.ec.frontend.infrastructure.mapper.ProductMapper;
import jp.co.fullness.ec.frontend.infrastructure.mapper.ProductStockMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final ProductStockMapper productStockMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final Factory<Product, ProductReconstructSource> productReconstructFactory;

    @Override
    public List<Product> searchByCategory(Integer categoryId) {
        List<Product> products = new ArrayList<>();
        for (ProductEntity entity : productMapper.searchByCategory(categoryId)) {
            products.add(assemble(entity));
        }
        return products;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(assemble(entity));
    }

    /** 商品Entityに在庫・カテゴリを補完してProduct集約を組み立てる。 */
    private Product assemble(ProductEntity productEntity) {
        ProductStockEntity stockEntity = productStockMapper.selectByProductId(productEntity.getId());
        ProductCategoryEntity categoryEntity = (productEntity.getProductCategoryId() != null)
                ? productCategoryMapper.selectById(productEntity.getProductCategoryId())
                : null;
        return productReconstructFactory.create(
                new ProductReconstructSource(productEntity, stockEntity, categoryEntity));
    }
}
