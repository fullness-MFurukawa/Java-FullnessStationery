package jp.co.fullness.ec.frontend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.ProductCategory;

public interface ProductCategoryRepository {
    List<ProductCategory> findAll();                 // カテゴリプルダウン
    Optional<ProductCategory> findById(Integer id);
}