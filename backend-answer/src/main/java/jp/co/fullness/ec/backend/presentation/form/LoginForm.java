package jp.co.fullness.ec.backend.presentation.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 担当者ログイン画面(BP002)の入力フォーム。
 */
@Getter
@Setter
public class LoginForm {

    /** アカウント名 */
    @NotBlank(message = "アカウント名を入力してください")
    private String username;

    /** パスワード */
    @NotBlank(message = "パスワードを入力してください")
    private String password;
}
