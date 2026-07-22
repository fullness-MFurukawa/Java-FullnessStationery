package jp.co.fullness.ec.frontend.domain.repository;

import java.util.List;
import java.util.Optional;

import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;

public interface PaymentMethodRepository {
    List<PaymentMethod> findAll();                 // 支払い方法プルダウン(現金)
    Optional<PaymentMethod> findById(Integer id);
}