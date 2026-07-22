package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductCategoryAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ ProductCategoryRepositoryImpl.class, ProductCategoryAdapter.class })
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
        ProductCategory category = productCategoryRepository.findById(1).orElseThrow();

        assertThat(category.getName()).isEqualTo("文房具");
    }
}