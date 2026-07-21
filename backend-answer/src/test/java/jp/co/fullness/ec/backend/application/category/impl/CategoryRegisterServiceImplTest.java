package jp.co.fullness.ec.backend.application.category.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryRegisterServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private CategoryRegisterServiceImpl service;

    @Test
    void register_カテゴリ名でProductCategoryを生成し登録する() {
        service.register("新カテゴリ");

        ArgumentCaptor<ProductCategory> captor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(productCategoryRepository).register(captor.capture());

        // 渡された ProductCategory に入力名が設定されている
        assertThat(captor.getValue().getName()).isEqualTo("新カテゴリ");
    }
}