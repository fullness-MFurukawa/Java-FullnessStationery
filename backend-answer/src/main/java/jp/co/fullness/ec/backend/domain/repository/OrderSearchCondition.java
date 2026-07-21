package jp.co.fullness.ec.backend.domain.repository;

import java.time.LocalDate;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 購入履歴検索(BP015)の検索条件。
 * ・orderDate : 購入日(日単位)。null の場合は日付で絞り込まない。
 * ・customerId: 顧客ID。null の場合は顧客で絞り込まない。
 * どちらも null の場合は全件が対象。
 */
@Getter
@AllArgsConstructor
public class OrderSearchCondition {
    private final LocalDate orderDate;
    private final Integer customerId;
}
