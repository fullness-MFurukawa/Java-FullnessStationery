package jp.co.fullness.ec.frontend.application.purchase;

import java.util.List;

import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;

public interface PurchaseService {

    /** 支払い方法の選択肢(現状は現金のみ)。FP009 表示用。 */
    List<PaymentMethod> findPaymentMethods();

    /**
     * 購入を確定する。在庫を悲観ロックして数量チェック・減算し、注文＋明細を登録する。
     *
     * @param customerId      ログイン顧客ID
     * @param paymentMethodId 選択された支払い方法ID
     * @return 登録した注文ID(FP010 表示・完了フラグ用)
     */
    Integer placeOrder(Integer customerId, Integer paymentMethodId);
}
