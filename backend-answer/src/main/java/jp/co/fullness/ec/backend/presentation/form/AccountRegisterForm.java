package jp.co.fullness.ec.backend.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 担当者アカウント登録(BP003)の入力フォーム。
 */
@Getter
@Setter
public class AccountRegisterForm {

    /** 社員ID(社員名の選択) */
    @NotNull(message = "社員名を選択してください")
    private Integer employeeId;

    /** アカウント名 */
    @NotBlank(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名は5〜20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "アカウント名は半角英数字で入力してください")
    private String accountName;

    /** パスワード */
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードは5〜20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "パスワードは半角英数字で入力してください")
    private String password;
}
