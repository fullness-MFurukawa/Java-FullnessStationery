package jp.co.fullness.ec.backend.application.order;

import java.util.List;

import jp.co.fullness.ec.backend.domain.model.Order;

import lombok.Getter;
import lombok.AllArgsConstructor;

/** 購入履歴検索(BP015)の結果＋ページング情報。 */
@Getter
@AllArgsConstructor
public class OrderSearchResult {
    private final List<Order> orders;
    private final long totalCount;
    private final int totalPages;
    private final int currentPage;
}