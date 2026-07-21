package jp.co.fullness.ec.backend.presentation.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品修正(BP009)の入力フォーム。
 * 画像は変更時のみ差し替え、未選択なら既存キー(imageKey)を維持する。
 */
@Getter
@Setter
public class ProductEditForm {

    /** 商品ID(修正対象) */
    private Integer productId;

    /** 商品名 */
    @NotBlank(message = "商品名を入力してください")
    @Size(min = 2, max = 20, message = "商品名は2〜20文字で入力してください")
    private String name;

    /** 単価 */
    @NotNull(message = "価格を入力してください")
    @Min(value = 0, message = "価格は0以上で入力してください")
    @Max(value = 1_000_000, message = "価格は100万円以下で入力してください")
    private Integer price;

    /** 在庫数 */
    @NotNull(message = "在庫数を入力してください")
    @Min(value = 0, message = "在庫数は0以上で入力してください")
    @Max(value = 1000, message = "在庫数は1000個以下で入力してください")
    private Integer stockQuantity;

    /** カテゴリID */
    @NotNull(message = "カテゴリを選択してください")
    private Integer categoryId;

    /** アップロード画像(変更時のみ。セッションには残さない) */
    private MultipartFile imageFile;

    /** 画像の S3 キー(既存キーをプリセット、変更時は新キー) */
    private String imageKey;

    /** 表示用ファイル名 */
    private String imageFileName;
}
