package jp.co.fullness.ec.backend.application.order;

import java.time.LocalDate;
import java.util.List;

import jp.co.fullness.ec.backend.domain.model.Customer;

public interface OrderSearchService {

    /** 条件(購入日/顧客ID、いずれも任意)で購入履歴を検索し、ページング付きで返す。 */
    OrderSearchResult search(LocalDate orderDate, Integer customerId, int page, int size);

    /** 購入日プルダウン用の日付一覧(新しい順)。 */
    List<LocalDate> findOrderDates();

    /** 顧客アカウント名プルダウン用の顧客一覧。 */
    List<Customer> findCustomers();
}