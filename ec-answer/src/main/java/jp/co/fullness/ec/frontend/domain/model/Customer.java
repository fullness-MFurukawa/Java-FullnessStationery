package jp.co.fullness.ec.frontend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 顧客(customer)を表すドメインオブジェクト。
 * password はハッシュ値(BCrypt)を保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /** 顧客ID */
    private Integer id;

    /** 顧客名 */
    private String name;

    /** 顧客名カナ */
    private String nameKana;

    /** 住所1 */
    private String address1;

    /** 住所2 */
    private String address2;

    /** 電話番号 */
    private String phoneNumber;

    /** メールアドレス */
    private String mailAddress;

    /** アカウント名 */
    private String username;

    /** パスワード(ハッシュ値) */
    private String password;

    /** 登録日時 */
    private LocalDateTime createdAt;
}
