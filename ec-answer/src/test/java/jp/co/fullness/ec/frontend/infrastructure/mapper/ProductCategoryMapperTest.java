package jp.co.fullness.ec.frontend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.frontend.infrastructure.entity.ProductCategoryEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductCategoryMapperTest {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @SuppressWarnings("null")
    @Test
    void selectAll_カテゴリを取得できる() {
        List<ProductCategoryEntity> categories = productCategoryMapper.selectAll();

        assertThat(categories).isNotEmpty();
        assertThat(categories).extracting(ProductCategoryEntity::getName).contains("文房具");
    }

    @Test
    void selectById_カテゴリを1件取得できる() {
        ProductCategoryEntity category = productCategoryMapper.selectById(1);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("文房具");
    }
}