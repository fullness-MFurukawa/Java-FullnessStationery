package jp.co.fullness.ec.backend.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品カテゴリ登録(BP019)の入力フォーム。
 */
@Getter
@Setter
public class CategoryRegisterForm {

    /** カテゴリ名 */
    @NotBlank(message = "カテゴリ名を入力してください")
    @Size(max = 30, message = "カテゴリ名は30文字以内で入力してください")
    private String name;
}