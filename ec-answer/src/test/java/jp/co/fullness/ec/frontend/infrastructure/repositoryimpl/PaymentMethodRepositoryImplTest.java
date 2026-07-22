package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.repository.PaymentMethodRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.PaymentMethodAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ PaymentMethodRepositoryImpl.class, PaymentMethodAdapter.class })
class PaymentMethodRepositoryImplTest {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @SuppressWarnings("null")
    @Test
    void findAll_現金を含む() {
        assertThat(paymentMethodRepository.findAll())
                .extracting(PaymentMethod::getName).contains("現金");
    }

    @Test
    void findById_現金を取得できる() {
        assertThat(paymentMethodRepository.findById(1).orElseThrow().getName())
                .isEqualTo("現金");
    }
}
