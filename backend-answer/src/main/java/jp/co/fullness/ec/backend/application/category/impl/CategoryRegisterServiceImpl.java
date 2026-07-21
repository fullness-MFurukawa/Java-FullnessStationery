package jp.co.fullness.ec.backend.application.category.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.category.CategoryRegisterService;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;

/**
 * {@link CategoryRegisterService} の実装。
 */
@Service
@RequiredArgsConstructor
public class CategoryRegisterServiceImpl implements CategoryRegisterService {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Transactional
    public void register(String name) {
        ProductCategory category = ProductCategory.builder().name(name).build();
        productCategoryRepository.register(category);
    }
}
