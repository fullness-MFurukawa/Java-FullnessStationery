package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;

@SpringBootTest
@Transactional
class ProductCategoryRepositoryImplTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @SuppressWarnings("null")
    @Test
    void findAll_カテゴリを取得できる() {
        List<ProductCategory> categories = productCategoryRepository.findAll();

        assertThat(categories).isNotEmpty();
        assertThat(categories).extracting(ProductCategory::getName).contains("文房具");
    }

    @Test
    void findById_カテゴリを1件取得できる() {
        ProductCategory category = productCategoryRepository.findById(1)
                .orElseThrow();

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("文房具");
    }
}