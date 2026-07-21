package jp.co.fullness.ec.backend.presentation.form;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品検索(BP006)の検索条件フォーム。
 */
@Getter
@Setter
public class ProductSearchForm {

    /** カテゴリID(null で全件) */
    private Integer categoryId;

    /** ページ番号(1始まり) */
    private int page = 1;
}
