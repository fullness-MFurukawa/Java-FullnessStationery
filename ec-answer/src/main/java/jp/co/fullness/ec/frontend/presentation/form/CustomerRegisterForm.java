package jp.co.fullness.ec.frontend.presentation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 顧客アカウント登録(FP003)の入力フォーム。
 */
@Getter
@Setter
public class CustomerRegisterForm {

    /** 氏名 */
    @NotBlank(message = "氏名を入力してください")
    @Size(min = 2, max = 20, message = "氏名は2〜20文字で入力してください")
    private String name;

    /** 氏名カナ(全角カナ) */
    @NotBlank(message = "氏名カナを入力してください")
    @Size(min = 2, max = 20, message = "氏名カナは2〜20文字で入力してください")
    @Pattern(regexp = "^[ァ-ヶー　]*$", message = "氏名カナは全角カナで入力してください")
    private String nameKana;

    /** 住所1 */
    @NotBlank(message = "住所1を入力してください")
    @Size(max = 100, message = "住所1は100文字以内で入力してください")
    private String address1;

    /** 住所2(任意) */
    @Size(max = 100, message = "住所2は100文字以内で入力してください")
    private String address2;

    /** 電話番号(XX-XXXX-XXXX) */
    @NotBlank(message = "電話番号を入力してください")
    @Pattern(regexp = "^(0\\d{1,4}-\\d{1,4}-\\d{4})?$",
             message = "電話番号はハイフン区切りで入力してください（例：090-1234-5678）")
    private String phoneNumber;

    /** メールアドレス */
    @NotBlank(message = "メールアドレスを入力してください")
    @Size(min = 4, max = 100, message = "メールアドレスは4〜100文字で入力してください")
    @Email(message = "正しいメールアドレス形式で入力してください")
    private String mailAddress;

    /** アカウント名(半角英数字) */
    @NotBlank(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名は5〜20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "アカウント名は半角英数字で入力してください")
    private String username;

    /** パスワード(半角英数字) */
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードは5〜20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "パスワードは半角英数字で入力してください")
    private String password;
}