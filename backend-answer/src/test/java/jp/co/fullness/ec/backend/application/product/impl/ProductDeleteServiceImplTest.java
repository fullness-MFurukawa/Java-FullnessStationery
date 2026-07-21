package jp.co.fullness.ec.backend.application.product.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.application.product.ProductSearchService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductDeleteServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchService productSearchService;

    @InjectMocks
    private ProductDeleteServiceImpl service;

    @Test
    void findById_有効な商品を返す() {
        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(false);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThat(service.findById(1)).isSameAs(product);
    }

    @Test
    void findById_存在しなければDomainException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void findById_削除済みならDomainException() {
        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.findById(1))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void generateImageUrl_検索サービスに委譲する() {
        when(productSearchService.generateImageUrl("products/x.png")).thenReturn("https://signed-url");

        assertThat(service.generateImageUrl("products/x.png")).isEqualTo("https://signed-url");
    }

    @Test
    void delete_有効な商品を論理削除する() {
        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(false);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        service.delete(1);

        verify(productRepository).logicalDelete(1);
    }

    @Test
    void delete_存在しなければ論理削除せずDomainException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(DomainException.class);
        verify(productRepository, never()).logicalDelete(anyInt());
    }

    @Test
    void delete_削除済みなら論理削除せずDomainException() {
        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.delete(1))
                .isInstanceOf(DomainException.class);
        verify(productRepository, never()).logicalDelete(anyInt());
    }
}