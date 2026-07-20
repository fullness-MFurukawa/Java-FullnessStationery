package jp.co.fullness.ec.backend.infrastructure.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * customer テーブルの1行を表す Entity。
 * password はハッシュ値(BCrypt)を保持する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {

    /** id */
    private Integer id;

    /** name */
    private String name;

    /** address1 */
    private String address1;

    /** address2 */
    private String address2;

    /** phone_number */
    private String phoneNumber;

    /** mail_address */
    private String mailAddress;

    /** username */
    private String username;

    /** password */
    private String password;

    /** created_at */
    private LocalDateTime createdAt;
}
