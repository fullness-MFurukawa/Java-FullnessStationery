package jp.co.fullness.ec.backend.presentation.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderSearchForm {

    /** 購入日(任意)。null は日付で絞り込まない。 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate orderDate;

    /** 顧客ID(任意)。null は顧客で絞り込まない。 */
    private Integer customerId;

    /** ページ番号(1始まり)。 */
    private Integer page = 1;
}